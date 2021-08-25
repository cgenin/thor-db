//! A library for creating interactive command line shells
#[macro_use]  extern crate prettytable;
extern crate clap;
extern crate termion;
extern crate edit;

mod command_line;
mod thorcli;
mod shell;
mod clients;


fn main() {
    let args = command_line::ThorCliArgs::new();
    println!("use hostname {:#?}", args.root_hostname.clone());
    let mut thor_shell = thorcli::ThorShell::new(args.root_hostname.clone());
    thor_shell.run();
}
