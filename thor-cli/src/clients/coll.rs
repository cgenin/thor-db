use serde::{Deserialize, Serialize};
use serde_json;

use crate::clients::client_utils::UrlHelper;

use super::client_utils::error_conversion;

#[allow(non_snake_case)]
#[derive(Debug, Serialize, Deserialize)]
pub struct CollectionOptions {
    disableMeta: bool,
    asyncListeners: bool,
    disableDeltaChangesApi: bool,
    disableChangesApi: bool,
    autoupdate: bool,
    disableFreeze: bool,
    clone: bool,
    removeIndices: bool,
}

#[allow(non_snake_case)]
#[derive(Debug, Serialize, Deserialize)]
pub struct Collection {
    pub name: String,
    pub options: CollectionOptions,
    pub cloneObjects: bool,
    pub maxId: u128,
    pub isIncremental: bool,
}


#[derive(Debug, Clone)]
pub struct CollectionClient {
    url_helper: UrlHelper,
}

impl CollectionClient {
    pub fn new(hostname: String) -> Self {
        return CollectionClient { url_helper: UrlHelper::new(hostname) };
    }

    pub fn list(&self, database: &str) -> Result<Vec<Collection>, String> {
        let url = self.url_helper.get_collection_url(database);
        let response = reqwest::blocking::get(url).map_err(|e| e.to_string())?;
        response.error_for_status_ref().map_err(error_conversion())?;
        return response
            .json::<Vec<Collection>>().map_err(error_conversion());
    }


    pub fn create(&self, database: &str, collection_name: &str) -> Result<Collection, String> {
        let url = self.url_helper.get_name_collection_url(database, collection_name);
        let client = reqwest::blocking::Client::new();
        let result = client.post(url)
            .send().map_err(error_conversion())?;
        result.error_for_status_ref().map_err(error_conversion())?;
        return result.json::<Collection>().map_err(error_conversion());
    }

    pub fn delete(&self, database: &str, collection_name: &str) -> Result<Collection, String> {
        let url = self.url_helper.get_name_collection_url(database, collection_name);
        let client = reqwest::blocking::Client::new();
        let response = client.post(url)
            .send()
            .map_err(error_conversion())?;
        response
            .error_for_status_ref()
            .map_err(error_conversion())?;
        return response
            .json::<Collection>()
            .map_err(error_conversion());
    }

    pub fn get(&self, database: &str, collection_name: &str) -> Result<Collection, String> {
        let url = self.url_helper.get_name_collection_url(database, collection_name);
        let client = reqwest::blocking::Client::new();
        let response = client.get(url)
            .send()
            .map_err(error_conversion())?;
        response
            .error_for_status_ref()
            .map_err(error_conversion())?;
        return response
            .json::<Collection>()
            .map_err(error_conversion());
    }

    pub fn get_data(&self, database: &str, collection_name: &str) -> Result<Vec<serde_json::Value>, String> {
        let url = self.url_helper.get_data_url(database, collection_name);
        let client = reqwest::blocking::Client::new();
        let response = client.get(url).send()
            .map_err(error_conversion())?;
        response
            .error_for_status_ref()
            .map_err(error_conversion())?;
        return response
            .json::<Vec<serde_json::Value>>()
            .map_err(error_conversion());
    }

    pub fn get_by_thorid(&self, database: &str, collection_name: &str, thorid : &str) -> Result<serde_json::Value, String> {
        let url = format!("{}/{}", self.url_helper.get_data_url(database, collection_name), thorid);
        let client = reqwest::blocking::Client::new();
        let response = client.get(url).send()
            .map_err(error_conversion())?;
        response
            .error_for_status_ref()
            .map_err(error_conversion())?;
        return response
            .json::<serde_json::Value>()
            .map_err(error_conversion());
    }

    pub fn count(&self, database: &str, collection_name: &str) -> Result<u128, String> {
        let url = format!("{}/count", self.url_helper.get_name_collection_url(database, collection_name));
        let client = reqwest::blocking::Client::new();
        let response = client.get(url).send()
            .map_err(error_conversion())?;
        response
            .error_for_status_ref()
            .map_err(error_conversion())?;
        return response
            .json::<u128>()
            .map_err(error_conversion());
    }


    pub fn insert_one(&self, database: &str, collection_name: &str, body: &str) -> Result<serde_json::Value, String> {
        let url = self.url_helper.get_data_url(database, collection_name);
        let b = serde_json::from_str::<serde_json::Value>(body)
            .map_err(|e| e.to_string())?;
        let response = reqwest::blocking::Client::new().post(url)
            .json(&b)
            .send()
            .map_err(error_conversion())?;
        response.error_for_status_ref().map_err(error_conversion())?;
        return response.json::<serde_json::Value>().map_err(error_conversion());
    }

    pub fn clear(&self, database: &str, collection_name: &str) -> Result<Vec<serde_json::Value>, String> {
        let url = self.url_helper.get_data_url(database, collection_name);
        let response = reqwest::blocking::Client::new().delete(url)
            .send()
            .map_err(error_conversion())?;
        response.error_for_status_ref().map_err(error_conversion())?;
        return response.json::<Vec<serde_json::Value>>().map_err(error_conversion());
    }

    pub fn insert(&self, database: &str, collection_name: &str, body: &str) -> Result<Vec<serde_json::Value>, String> {
        let url = format!("{}/insert", self.url_helper.get_name_collection_url(database, collection_name));
        let b = serde_json::from_str::<Vec<serde_json::Value>>(body)
            .map_err(|e| e.to_string())?;
        let response = reqwest::blocking::Client::new().post(url)
            .json(&b)
            .send()
            .map_err(error_conversion())?;
        response.error_for_status_ref().map_err(error_conversion())?;
        return response.json::<Vec<serde_json::Value>>().map_err(error_conversion());
    }
}