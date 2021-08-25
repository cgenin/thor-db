use serde::{Deserialize, Serialize};

use crate::clients::client_utils::UrlHelper;

use super::client_utils::error_conversion;

#[derive(Debug, Serialize, Deserialize)]
pub struct Database {
    pub name: String,
}

#[derive(Debug, Clone)]
pub struct DatabaseClient {
    url_helper: UrlHelper,
}


impl DatabaseClient {
    pub fn new(hostname: String) -> Self {
        return DatabaseClient { url_helper: UrlHelper::new(hostname) };
    }

    pub fn list(&self) -> Result<Vec<Database>, String> {
        let url = self.url_helper.get_database_url();
        let response = reqwest::blocking::get(url).map_err(error_conversion())?;
        response.error_for_status_ref().map_err(error_conversion())?;
        return response
            .json::<Vec<Database>>().map_err(error_conversion());
    }


    pub fn create(&self, new_db: &str) -> Result<Database, String> {
        let url = self.url_helper.get_name_database_url(new_db);
        let response = reqwest::blocking::Client::new().post(url)
            .send().map_err(error_conversion())?;
        response.error_for_status_ref().map_err(error_conversion())?;
        return response.json::<Database>()
            .map_err(error_conversion());
    }
}

