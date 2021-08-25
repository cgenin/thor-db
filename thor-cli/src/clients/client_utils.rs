use reqwest::Error;

pub fn error_conversion() -> fn(Error) -> String {
    |e| e.to_string()
}

#[derive(Debug, Clone)]
pub struct UrlHelper {
    hostname: String,
}

impl UrlHelper {
    pub fn new(hostname: String) -> Self {
        UrlHelper {
            hostname
        }
    }

    pub fn get_database_url(&self) -> String {
        format!("{}{}", self.hostname, "/api/databases")
    }

    pub fn get_name_database_url(&self, database: &str) -> String {
        format!("{}/{}", self.get_database_url(), database)
    }

    pub fn get_collection_url(&self, database: &str) -> String {
        format!("{}{}/{}/collections", self.hostname, "/api/databases", database)
    }

    pub fn get_name_collection_url(&self, database: &str, collection: &str) -> String {
        return format!("{}/{}", self.get_collection_url(database), collection);
    }

    pub fn get_data_url(&self, database: &str, new_coll: &str) -> String {
        format!("{}/data", self.get_name_collection_url(database, new_coll))
    }
}
