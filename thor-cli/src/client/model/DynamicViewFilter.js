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

import ApiClient from '../ApiClient';

/**
* The DynamicViewFilter model module.
* @module model/DynamicViewFilter
* @version 1.0.0
*/
export default class DynamicViewFilter {
    /**
    * Constructs a new <code>DynamicViewFilter</code>.
    * @alias module:model/DynamicViewFilter
    * @class
    */

    constructor() {



    }

    /**
    * Constructs a <code>DynamicViewFilter</code> from a plain JavaScript object, optionally creating a new instance.
    * Copies all relevant properties from <code>data</code> to <code>obj</code> if supplied or a new instance if not.
    * @param {Object} data The plain JavaScript object bearing properties of interest.
    * @param {module:model/DynamicViewFilter} obj Optional instance to populate.
    * @return {module:model/DynamicViewFilter} The populated <code>DynamicViewFilter</code> instance.
    */
    static constructFromObject(data, obj) {
        if (data) {
            obj = obj || new DynamicViewFilter();


            if (data.hasOwnProperty('type')) {
                obj['type'] = ApiClient.convertToType(data['type'], 'String');
            }
            if (data.hasOwnProperty('uid')) {
                obj['uid'] = ApiClient.convertToType(data['uid'], 'String');
            }
            if (data.hasOwnProperty('val')) {
                obj['val'] = ApiClient.convertToType(data['val'], Object);
            }
        }
        return obj;
    }





}