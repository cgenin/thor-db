use clap::{App, Arg};

pub struct ThorCliArgs {
    pub root_hostname: String,
}

impl ThorCliArgs {
    pub fn new() -> ThorCliArgs {
        let app_clap: App = App::new("Thor client")
            .version("0.1.0")
            .author("Genin Christophe <genin.christophe@gmail.com>")
            .about("An thor client for communicate with Thor servre")
            .arg(Arg::with_name("host")
                .short("h")
                .long("host")
                .value_name("HOSTNAME")
                .help("The hostname of the Thor's srver")
                .takes_value(true));
        let arg_matcher = app_clap.get_matches();
        let host = arg_matcher.value_of("host");
        let root_hostname = host.unwrap_or("http://localhost:8080").to_string();
        ThorCliArgs {
            root_hostname
        }
    }
}
