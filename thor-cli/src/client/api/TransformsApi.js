/**
 * Database API
 * No description provided (generated by Swagger Codegen https://github.com/swagger-api/swagger-codegen)
 *
 * OpenAPI spec version: 1.0.0
 * 
 *
 * NOTE: This class is auto generated by the swagger code generator program.
 * https://github.com/swagger-api/swagger-codegen.git
 * Do not edit the class manually.
 *
 */

import ApiClient from "../ApiClient";
import TransformDto from '../model/TransformDto';

/**
* Transforms service.
* @module api/TransformsApi
* @version 1.0.0
*/
export default class TransformsApi {

    /**
    * Constructs a new TransformsApi. 
    * @alias module:api/TransformsApi
    * @class
    * @param {module:ApiClient} [apiClient] Optional API client implementation to use,
    * default to {@link module:ApiClient#instance} if unspecified.
    */
    constructor(apiClient) {
        this.apiClient = apiClient || ApiClient.instance;
    }

    /**
     * Callback function to receive the result of the apiDatabasesdatabaseNameCollectionscollectionNameTransformsGet operation.
     * @callback module:api/TransformsApi~apiDatabasesdatabaseNameCollectionscollectionNameTransformsGetCallback
     * @param {String} error Error message, if any.
     * @param {Array.<module:model/TransformDto>} data The data returned by the service call.
     * @param {String} response The complete HTTP response.
     */

    /**
     * List
     * List all registered map&#x27;s operations
     * @param {Object} opts Optional parameters
     * @param {module:api/TransformsApi~apiDatabasesdatabaseNameCollectionscollectionNameTransformsGetCallback} callback The callback function, accepting three arguments: error, data, response
     * data is of type: {@link Array.<module:model/TransformDto>}
     */
    apiDatabasesdatabaseNameCollectionscollectionNameTransformsGet(opts, callback) {
      opts = opts || {};
      let postBody = null;

      let pathParams = {
      };
      let queryParams = {
        'collectionName': opts['collectionName'],
        'databaseName': opts['databaseName']
      };
      let headerParams = {
      };
      let formParams = {
      };

      let authNames = [];
      let contentTypes = [];
      let accepts = ['application/json'];
      let returnType = [TransformDto];

      return ApiClient.instance.callApi(
        '/api/databases/:databaseName/collections/:collectionName/transforms', 'GET',
        pathParams, queryParams, headerParams, formParams, postBody,
        authNames, contentTypes, accepts, returnType, callback
      );
    }
    /**
     * Callback function to receive the result of the apiDatabasesdatabaseNameCollectionscollectionNameTransformsPost operation.
     * @callback module:api/TransformsApi~apiDatabasesdatabaseNameCollectionscollectionNameTransformsPostCallback
     * @param {String} error Error message, if any.
     * @param {'Boolean'} data The data returned by the service call.
     * @param {String} response The complete HTTP response.
     */

    /**
     * Create
     * Create an registered map&#x27;s operations
     * @param {Object} opts Optional parameters
     * @param {module:api/TransformsApi~apiDatabasesdatabaseNameCollectionscollectionNameTransformsPostCallback} callback The callback function, accepting three arguments: error, data, response
     * data is of type: {@link 'Boolean'}
     */
    apiDatabasesdatabaseNameCollectionscollectionNameTransformsPost(opts, callback) {
      opts = opts || {};
      let postBody = opts['body'];

      let pathParams = {
      };
      let queryParams = {
        'collectionName': opts['collectionName'],
        'databaseName': opts['databaseName']
      };
      let headerParams = {
      };
      let formParams = {
      };

      let authNames = [];
      let contentTypes = ['application/json'];
      let accepts = ['application/json'];
      let returnType = 'Boolean';

      return ApiClient.instance.callApi(
        '/api/databases/:databaseName/collections/:collectionName/transforms', 'POST',
        pathParams, queryParams, headerParams, formParams, postBody,
        authNames, contentTypes, accepts, returnType, callback
      );
    }
    /**
     * Callback function to receive the result of the apiDatabasesdatabaseNameCollectionscollectionNameTransformsTransformNameDelete operation.
     * @callback module:api/TransformsApi~apiDatabasesdatabaseNameCollectionscollectionNameTransformsTransformNameDeleteCallback
     * @param {String} error Error message, if any.
     * @param {'Boolean'} data The data returned by the service call.
     * @param {String} response The complete HTTP response.
     */

    /**
     * Delete
     * Delete an registered map&#x27;s operations
     * @param {Object} opts Optional parameters
     * @param {module:api/TransformsApi~apiDatabasesdatabaseNameCollectionscollectionNameTransformsTransformNameDeleteCallback} callback The callback function, accepting three arguments: error, data, response
     * data is of type: {@link 'Boolean'}
     */
    apiDatabasesdatabaseNameCollectionscollectionNameTransformsTransformNameDelete(transformName, opts, callback) {
      opts = opts || {};
      let postBody = null;

      let pathParams = {
        'transformName': transformName
      };
      let queryParams = {
        'collectionName': opts['collectionName'],
        'databaseName': opts['databaseName']
      };
      let headerParams = {
      };
      let formParams = {
      };

      let authNames = [];
      let contentTypes = [];
      let accepts = ['application/json'];
      let returnType = 'Boolean';

      return ApiClient.instance.callApi(
        '/api/databases/:databaseName/collections/:collectionName/transforms/{transformName}', 'DELETE',
        pathParams, queryParams, headerParams, formParams, postBody,
        authNames, contentTypes, accepts, returnType, callback
      );
    }

}