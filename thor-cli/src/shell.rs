use std::collections::BTreeMap;
use std::error::Error;
use std::fmt;
use std::io;
use std::io::prelude::*;
use std::ops::{Deref, DerefMut};
use std::string::ToString;
use std::sync::{Arc, Mutex};

use prettytable::format;
use prettytable::Table;
use termion::{color};

use self::ExecError::*;

/// Command execution error
#[derive(Debug)]
pub enum ExecError {
    /// Empty command provided
    Empty,
    /// Exit from the shell loop
    Quit,
    /// Some arguments are missing
    MissingArgs,
    /// The provided command is unknown
    UnknownCommand(String),
    /// The history index is not valid
    InvalidHistory(usize),
    /// Other error that may have happen during command execution
    Other(Box<dyn Error>),
}

impl fmt::Display for ExecError {
    fn fmt(&self, format: &mut fmt::Formatter) -> fmt::Result {
        return match *self {
            Empty => write!(format, "No command provided"),
            Quit => write!(format, "Quit"),
            UnknownCommand(ref cmd) => write!(format, "Unknown Command {}", cmd),
            InvalidHistory(i) => write!(format, "Invalid history entry {}", i),
            MissingArgs => write!(format, "Not enough arguments"),
            Other(ref e) => write!(format, "{}", e)
        };
    }
}


impl<E: Error + 'static> From<E> for ExecError {
    fn from(e: E) -> ExecError {
        return Other(Box::new(e));
    }
}

/// Input / Output for shell execution
#[derive(Clone)]
pub struct ShellIO {
    input: Arc<Mutex<dyn io::Read + Send>>,
    output: Arc<Mutex<dyn io::Write + Send>>,
}

impl ShellIO {
    /// Create a new Shell I/O wrapping provided Input and Output
    pub fn new<I, O>(input: I, output: O) -> ShellIO
        where I: Read + Send + 'static, O: Write + Send + 'static
    {
        return ShellIO {
            input: Arc::new(Mutex::new(input)),
            output: Arc::new(Mutex::new(output)),
        };
    }

}

impl Read for ShellIO {
    fn read(&mut self, buf: &mut [u8]) -> io::Result<usize> {
        return self.input.lock().expect("Cannot get handle to console input").read(buf);
    }
}

impl Write for ShellIO {
    fn write(&mut self, buf: &[u8]) -> io::Result<usize> {
        return self.output.lock().expect("Cannot get handle to console output").write(buf);
    }

    fn flush(&mut self) -> io::Result<()> {
        return self.output.lock().expect("Cannot get handle to console output").flush();
    }
}

impl Default for ShellIO {
    fn default() -> Self {
        return Self::new(io::stdin(), io::stdout());
    }
}

impl ShellIO {
    pub fn info(&mut self, write: String) {
        writeln!(self, "{}{}", color::Fg(color::Blue), write).unwrap();
        write!(self, "{}", color::Fg(color::Reset)).unwrap();
    }

    pub fn info_str(&mut self, write: &str) {
        writeln!(self, "{}{}", color::Fg(color::Blue), write).unwrap();
        write!(self, "{}", color::Fg(color::Reset)).unwrap();
    }

    pub fn error(&mut self, write: String) {
        writeln!(self, "{}{}", color::Fg(color::Red), write).unwrap();
        write!(self, "{}", color::Fg(color::Reset)).unwrap();
    }

    pub fn error_str(&mut self, write: &str) {
        writeln!(self, "{}{}", color::Fg(color::Red), write).unwrap();
        write!(self, "{}", color::Fg(color::Reset)).unwrap();
    }
}

/// Result from command execution
pub type ExecResult = Result<(), ExecError>;


pub struct SharedDataShell {
    pub root_hostname: String,
    pub database: Option<String>,
    pub collection: Option<String>,

}


impl Clone for SharedDataShell {
    fn clone(&self) -> Self {
        SharedDataShell {
            root_hostname: self.root_hostname.clone(),
            database: self.database.clone(),
            collection: self.collection.clone(),
        }
    }
}


/// A shell
pub struct Shell {
    commands: BTreeMap<String, Arc<builtins::Command>>,
    default: Arc<dyn Fn(&mut ShellIO, &mut Shell, &str) -> ExecResult + Send + Sync>,
    data: SharedDataShell,
    default_prompt: String,
    unclosed_prompt: String,
    history: History,
}

impl Shell {
    /// Create a new shell, wrapping `data`, using provided IO
    pub fn new(data: SharedDataShell) -> Shell {
        let mut sh = Shell {
            commands: BTreeMap::new(),
            default: Arc::new(|_, _, cmd| Err(UnknownCommand(cmd.to_string()))),
            data,
            default_prompt: String::from(">"),
            unclosed_prompt: String::from(">"),
            history: History::new(10),
        };
        sh.register_command(builtins::help_cmd());
        sh.register_command(builtins::quit_cmd());
        sh.register_command(builtins::history_cmd());
        return sh;
    }


    /// Get a mutable pointer to the inner data
    pub fn data(&mut self) -> &mut SharedDataShell {
        return &mut self.data;
    }

    fn register_command(&mut self, cmd: builtins::Command) {
        self.commands.insert(cmd.name.clone(), Arc::new(cmd));
    }

    // Set a custom default handler, invoked when a command is not found
    pub fn set_default<F>(&mut self, func: F)
        where F: Fn(&mut ShellIO, &mut Shell, &str) -> ExecResult + Send + Sync + 'static
    {
        self.default = Arc::new(func);
    }

    /// Register a shell command.
    /// Shell commands get called with a reference to the current shell
    pub fn new_shell_command<S, F>(&mut self, name: S, description: S, nargs: usize, func: F)
        where S: ToString, F: Fn(&mut ShellIO, &mut Shell, &[&str]) -> ExecResult + Send + Sync + 'static
    {
        self.register_command(builtins::Command::new(name.to_string(), description.to_string(), nargs, Box::new(func)));
    }

    /// Register a command
    pub fn new_command<S, F>(&mut self, name: S, description: S, nargs: usize, func: F)
        where S: ToString, F: Fn(&mut ShellIO, &mut SharedDataShell, &[&str]) -> ExecResult + Send + Sync + 'static
    {
        self.new_shell_command(name, description, nargs, move |io, sh, args| func(io, sh.data(), args));
    }

    /// Register a command that do not accept any argument
    pub fn new_command_noargs<S, F>(&mut self, name: S, description: S, func: F)
        where S: ToString, F: Fn(&mut ShellIO, &mut SharedDataShell) -> ExecResult + Send + Sync + 'static
    {
        self.new_shell_command(name, description, 0, move |io, sh, _| func(io, sh.data()));
    }

    /// Print the help to stdout
    pub fn print_help(&self, io: &mut ShellIO) -> ExecResult {
        let mut table = Table::new();
        table.set_format(*format::consts::FORMAT_CLEAN);
        for cmd in self.commands.values() {
            table.add_row(cmd.help());
        }
        table.print(io)?;
        Ok(())
    }

    /// Return the command history
    pub fn get_history(&self) -> &History {
        return &self.history;
    }

    /// Evaluate a command line
    pub fn eval(&mut self, io: &mut ShellIO, line: &str) -> ExecResult {
        let mut splt = line.trim().split_whitespace();
        return match splt.next() {
            None => Err(Empty),
            Some(cmd) => match self.commands.get(cmd).cloned() {
                None => self.default.clone()(io, self, line),
                Some(c) => c.run(io, self, &splt.collect::<Vec<&str>>())
            }
        };
    }

    fn print_prompt(&self, io: &mut ShellIO, unclosed: bool) {
        let database = self.data.database.clone().unwrap_or("".to_string());
        let collection = self.data.collection.clone().unwrap_or("".to_string());
        if unclosed {
            write!(io, "{}/{}/{} ", database, collection, self.unclosed_prompt).unwrap();
        } else {
            write!(io, "{}/{}/{} ", database, collection, self.default_prompt).unwrap();
        }
        io.flush().unwrap();
    }

    /// Enter the shell main loop, exiting only when
    /// the "quit" command is called
    pub fn run_loop(&mut self, io: &mut ShellIO) {
        self.print_prompt(io, false);
        let stdin = io::BufReader::new(io.clone());
        let mut iter = stdin.lines().map(|l| l.unwrap());
        while let Some(mut line) = iter.next() {
            while !line.is_empty() && &line[line.len() - 1..] == "\\" {
                self.print_prompt(io, true);
                line.pop();
                line.push_str(&iter.next().unwrap())
            }
            if let Err(e) = self.eval(io, &line) {
                match e {
                    Empty => {}
                    Quit => return,
                    e => writeln!(io, "Error : {}", e).unwrap()
                };
            } else {
                self.get_history().push(line);
            }
            self.print_prompt(io, false);
        }
    }
}

impl Deref for Shell {
    type Target = SharedDataShell;
    fn deref(&self) -> &SharedDataShell {
        return &self.data;
    }
}

impl DerefMut for Shell {
    fn deref_mut(&mut self) -> &mut SharedDataShell {
        return &mut self.data;
    }
}

impl Clone for Shell {
    fn clone(&self) -> Self {
        return Shell {
            commands: self.commands.clone(),
            default: self.default.clone(),
            data: self.data.clone(),
            default_prompt: self.default_prompt.clone(),
            unclosed_prompt: self.unclosed_prompt.clone(),
            history: self.history.clone(),
        };
    }
}

/// Wrap the command history from a shell.
/// It has a maximum capacity, and when max capacity is reached,
/// less recent command is removed from history
#[derive(Clone)]
pub struct History {
    history: Arc<Mutex<Vec<String>>>,
    capacity: usize,
}

impl History {
    /// Create a new history with the given capacity
    fn new(capacity: usize) -> History {
        return History {
            history: Arc::new(Mutex::new(Vec::with_capacity(capacity))),
            capacity,
        };
    }

    /// Push a command to the history, removing the oldest
    /// one if maximum capacity has been reached
    fn push(&self, cmd: String) {
        let mut hist = self.history.lock().unwrap();
        if hist.len() >= self.capacity {
            hist.remove(0);
        }
        hist.push(cmd);
    }

    /// Print the history to stdout
    pub fn print<T: Write>(&self, out: &mut T) {
        let mut cnt = 0;
        for s in &*self.history.lock().unwrap() {
            writeln!(out, "{}: {}", cnt, s).expect("Cannot write to output");
            cnt += 1;
        }
    }

    /// Get a command from history by its index
    pub fn get(&self, i: usize) -> Option<String> {
        return self.history.lock().unwrap().get(i).cloned();
    }
}

mod builtins {
    use std::str::FromStr;

    use prettytable::Row;

    use super::{ExecError, ExecResult, Shell, ShellIO};

    pub type CmdFn<T> = Box<dyn Fn(&mut ShellIO, &mut T, &[&str]) -> ExecResult + Send + Sync>;

    pub struct Command {
        pub name: String,
        description: String,
        nargs: usize,
        func: CmdFn<Shell>,
    }

    impl Command {
        pub fn new(name: String, description: String, nargs: usize, func: CmdFn<Shell>) -> Command {
            return Command {
                name,
                description,
                nargs,
                func,
            };
        }

        pub fn help(&self) -> Row {
            return row![self.name, ":", self.description];
        }

        pub fn run(&self, io: &mut ShellIO, shell: &mut Shell, args: &[&str]) -> ExecResult {
            if args.len() < self.nargs {
                return Err(ExecError::MissingArgs);
            }
            return (self.func)(io, shell, args);
        }
    }

    pub fn help_cmd() -> Command {
        return Command::new("help".to_string(), "Print this help".to_string(), 0, Box::new(|io, shell, _| shell.print_help(io)));
    }

    pub fn quit_cmd() -> Command {
        return Command::new("quit".to_string(), "Quit".to_string(), 0, Box::new(|_, _, _| Err(ExecError::Quit)));
    }

    pub fn history_cmd() -> Command {
        return Command::new("history".to_string(), "Print commands history or run a command from it".to_string(), 0, Box::new(|io, shell, args| {
            if !args.is_empty() {
                let i = usize::from_str(args[0])?;
                let cmd = shell.get_history().get(i).ok_or_else(|| ExecError::InvalidHistory(i))?;
                return shell.eval(io, &cmd);
            } else {
                shell.get_history().print(io);
                return Ok(());
            }
        }));
    }
}