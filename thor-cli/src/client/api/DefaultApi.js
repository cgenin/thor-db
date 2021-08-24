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
import Health from '../model/Health';

/**
* Default service.
* @module api/DefaultApi
* @version 1.0.0
*/
export default class DefaultApi {

    /**
    * Constructs a new DefaultApi.
    * @alias module:api/DefaultApi
    * @class
    * @param {module:ApiClient} [apiClient] Optional API client implementation to use,
    * default to {@link module:ApiClient#instance} if unspecified.
    */
    constructor(apiClient) {
        this.apiClient = apiClient || ApiClient.instance;
    }

    /**
     * Callback function to receive the result of the apiHealthGet operation.
     * @callback module:api/DefaultApi~apiHealthGetCallback
     * @param {String} error Error message, if any.
     * @param {module:model/Health} data The data returned by the service call.
     * @param {String} response The complete HTTP response.
     */

    /**
     * @param {module:api/DefaultApi~apiHealthGetCallback} callback The callback function, accepting three arguments: error, data, response
     * data is of type: {@link module:model/Health}
     */
    apiHealthGet(callback) {
      let postBody = null;

      let pathParams = {
      };
      let queryParams = {
      };
      let headerParams = {
      };
      let formParams = {
      };

      let authNames = [];
      let contentTypes = [];
      let accepts = ['application/json'];
      let returnType = Health;

      return ApiClient.instance.callApi(
        '/api/health', 'GET',
        pathParams, queryParams, headerParams, formParams, postBody,
        authNames, contentTypes, accepts, returnType, callback
      );
    }

}
