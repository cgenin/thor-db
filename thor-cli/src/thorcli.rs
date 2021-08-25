use edit;
use prettytable::Table;
use serde::{Deserialize, Serialize};
use serde_json::Value;

use super::clients::coll;
use super::clients::db;
use super::shell::{SharedDataShell, Shell, ShellIO};

#[allow(non_snake_case)]
#[derive(Debug, Serialize, Deserialize)]
struct Ping {
    pub up: bool,
    pub nbDatabases: u32,
}

fn ping(args: &str) -> reqwest::Result<Ping> {
    let url = format!("{}{}", args, "/api/health");
    return reqwest::blocking::get(url)?
        .json::<Ping>();
}

fn show_data(io: &mut ShellIO, collections: Vec<Value>) {
    if collections.is_empty() {
        io.info_str("No data");
    } else {
        let mut table = Table::new();
        table.set_titles(row!["data"]);
        for collection in collections {
            let json = serde_json::to_string_pretty(&collection).unwrap();
            table.add_row(row![json]);
        }
        io.info(table.to_string())
    }
}


pub struct ThorShell {
    pub shell: Shell,
}

fn verify_context(shared: SharedDataShell, io: &mut ShellIO) -> bool {
    if shared.database.is_none() {
        io.error_str("No Database selected");
        return false;
    }
    if shared.collection.is_none() {
        io.error_str("No Collection selected");
        return false;
    }
    return true;
}

impl ThorShell {
    pub fn new(root_hostname: String) -> ThorShell {
        let shared_data_shell = SharedDataShell { database: None, collection: None, root_hostname: root_hostname.clone() };


        let shell = Shell::new(shared_data_shell);

        let mut thor = ThorShell {
            shell,
        };
        thor.shell.set_default(|io, _, _| {
            io.error_str("Command not found. Use 'help' for known commands.");
            Ok(())
        });
        thor.register_infrastructure();
        thor.register_database();
        thor.register_collection();
        thor
    }


    pub fn register_infrastructure(&mut self) {
        self.shell.new_command_noargs("ping", "ping the server", move |io, shared| {
            let res = ping(shared.root_hostname.as_str());
            match res {
                Ok(p) => if p.up {
                    io.info_str("The server is up");
                } else {
                    io.info_str("the server is down");
                }
                Err(_) => io.error_str("the server is down")
            }

            Ok(())
        });

        self.shell.new_command("usedb", "Set the current database", 1, move |io, shared, args| {
            if args.len() == 1 {
                let databasename = args.get(0).unwrap();
                shared.database = Some(databasename.to_string());
                shared.collection = None;
                io.info(format!("registered to db : {}", databasename));
            }
            Ok(())
        });

        self.shell.new_command("usecoll", "Set the current collection", 1, move |io, shared, args| {
            if args.len() == 1 {
                let collection = args.get(0).unwrap();
                shared.collection = Some(collection.to_string());
                io.info(format!("registered to collection : {}", collection));
            }
            Ok(())
        });

        self.shell.new_command_noargs("reset", "Reset the terminal session.", move |_, shared| {
            shared.collection = None;
            shared.database = None;
            Ok(())
        });
    }

    pub fn register_database(&mut self) {
        self.shell.new_command_noargs("listdb", "List databases", |io, shared| {
            let database_client = db::DatabaseClient::new(shared.root_hostname.clone());
            let resp = database_client.list().unwrap_or(Vec::new());
            let mut table = Table::new();
            table.set_titles(row!["Database name"]);
            for db in resp.iter() {
                table.add_row(row![db.name]);
            }
            io.info(table.to_string());

            Ok(())
        });

        self.shell.new_command("createdb", "Create an database", 1, |io, shared, args| {
            let database_client = db::DatabaseClient::new(shared.root_hostname.clone());
            let new_db = args.get(0).unwrap();

            let resp = database_client.create(new_db);
            match resp {
                Ok(db) => io.info(format!("- {:#?}", db.name)),
                Err(e) => io.error(format!("Error in creating collection {}", e.to_string()))
            }
            Ok(())
        });
    }

    pub fn register_collection(&mut self) {
        self.shell.new_command_noargs("listcoll", "List collections", |io, shared| {
            if shared.database.is_some() {
                let database = shared.database.as_ref().unwrap();
                let hostname = shared.root_hostname.clone();
                let collection_client = coll::CollectionClient::new(hostname);
                let collections = collection_client.list(database.as_str())
                    .unwrap_or(Vec::new());
                let mut table = Table::new();
                table.set_titles(row![format!("{}'s collections", database)]);
                for collection in collections {
                    table.add_row(row![collection.name]);
                }
                io.info(table.to_string());
            } else {
                io.error_str("No database selected - select with 'usedb'");
            }
            Ok(())
        });

        self.shell.new_command("createcoll", "Create an collection", 1, |io, shared, args| {
            if shared.database.is_some() {
                let collection_client = coll::CollectionClient::new(shared.root_hostname.clone());
                let new_coll = args.get(0).unwrap();
                let database = shared.database.as_ref().unwrap();
                let resp = collection_client.create(database, new_coll);
                match resp {
                    Ok(c) =>
                        io.info(format!("new Collection created : {:#?}", c.name)),
                    Err(e) =>
                        io.error(format!("Error in creating collection {}", e.to_string()))
                }
            } else {
                io.error_str("No database selected.");
            }
            Ok(())
        });

        self.shell.new_command("deletecoll", "Delete an collection", 1, |io, shared, args| {
            let collection_client = coll::CollectionClient::new(shared.root_hostname.clone());
            let new_coll = args.get(0).unwrap();
            let database = shared.database.as_ref().unwrap();
            let resp = collection_client.delete(database, new_coll);
            match resp {
                Ok(c) => io.info(format!("Collection deleted : {:#?}", c.name)),
                Err(e) => io.error(format!("Error in creating collection {}", e.to_string()))
            }
            Ok(())
        });


        self.shell.new_command_noargs("info", "Get collection's description", |io, shared| {
            if verify_context(shared.clone(), io) {
                let collection_client = coll::CollectionClient::new(shared.root_hostname.clone());
                let database = shared.database.as_ref().unwrap();
                let collection = shared.collection.as_ref().unwrap();
                let resp = collection_client.get(database, collection);
                match resp {
                    Ok(c) => io.info(format!("Collection Description : {:#?}", c)),
                    Err(e) => io.error(format!("Error in getting information {}", e))
                }
            }
            Ok(())
        });

        self.shell.new_command("add", "Insert One json object in collection's data.", 1, |io, shared, args| {
            if verify_context(shared.clone(), io) {
                let collection_client = coll::CollectionClient::new(shared.root_hostname.clone());
                let database = shared.database.as_ref().unwrap();
                let collection = shared.collection.as_ref().unwrap();
                let json_obj = args.to_vec().join("");
                let resp = collection_client.insert_one(database, collection, &json_obj);
                match resp {
                    Ok(c) => {
                        let result = serde_json::to_string_pretty(&c)?;
                        io.info(format!("{}", result));
                    }
                    Err(e) => io.error(format!("Error in adding an json {}", e))
                }
            }
            Ok(())
        });

        self.shell.new_command("getById", "Get one object by id.", 1, |io, shared, args| {
            if verify_context(shared.clone(), io) {
                let collection_client = coll::CollectionClient::new(shared.root_hostname.clone());
                let database = shared.database.as_ref().unwrap();
                let collection = shared.collection.as_ref().unwrap();
                let thorid = args.get(0).unwrap();
                let resp = collection_client.get_by_thorid(database, collection, thorid);
                match resp {
                    Ok(c) => {
                        let result = serde_json::to_string_pretty(&c)?;
                        io.info(format!("{}", result));
                    }
                    Err(e) => io.error(format!("Error in getting Json for {} : {}", thorid, e))
                }
            }
            Ok(())
        });

        self.shell.new_command_noargs("data", "Get data from the collection", |io, shared| {
            if verify_context(shared.clone(), io) {
                let collection_client = coll::CollectionClient::new(shared.root_hostname.clone());
                let database = shared.database.as_ref().unwrap();
                let collection = shared.collection.as_ref().unwrap();
                let resp = collection_client.get_data(database, collection);
                match resp {
                    Ok(collections) => show_data(io, collections),
                    Err(e) => io.error(format!("Error in getting data : {:#?}", e))
                }
            }
            Ok(())
        });

        self.shell.new_command_noargs("count", "The size of collection's data.", |io, shared| {
            if verify_context(shared.clone(), io) {
                let collection_client = coll::CollectionClient::new(shared.root_hostname.clone());
                let database = shared.database.as_ref().unwrap();
                let collection = shared.collection.as_ref().unwrap();
                let resp = collection_client.count(database, collection);
                match resp {
                    Ok(size) => io.info(format!("Size : {}", size)),
                    Err(e) => io.error(format!("Error in counting data : {:#?}", e))
                }
            }
            Ok(())
        });
        self.shell.new_command_noargs("clear", "Clear all data.", |io, shared| {
            if verify_context(shared.clone(), io) {
                let collection_client = coll::CollectionClient::new(shared.root_hostname.clone());
                let database = shared.database.as_ref().unwrap();
                let collection = shared.collection.as_ref().unwrap();
                let resp = collection_client.clear(database, collection);
                match resp {
                    Ok(collections) => show_data(io, collections),
                    Err(e) => io.error(format!("Error in counting data : {:#?}", e))
                }
            }
            Ok(())
        });


        self.shell.new_command_noargs("insert", "Insert an array of json object in current collection.", |io, shared| {
            if verify_context(shared.clone(), io) {
                let template = r#"
                    [
                        {"test":"value 1"},
                        {"test":"value 2"}
                    ]
                "#;
                let edited = edit::edit(template)?;
                io.info(format!("Used :  {}", edited));
                let collection_client = coll::CollectionClient::new(shared.root_hostname.clone());
                let database = shared.database.as_ref().unwrap();
                let collection = shared.collection.as_ref().unwrap();
                let resp = collection_client.insert(database, collection, &edited);
                match resp {
                    Ok(collections) => show_data(io, collections),
                    Err(e) => io.error(format!("Error in getting data : {:#?}", e))
                }
            }
            Ok(())
        });
    }


    pub fn run(&mut self) {
        self.shell.run_loop(&mut ShellIO::default());
    }
}