import { DefaultApi } from './client';
import { bindNodeCallback } from 'rxjs';


export default function () {
    let defaultApi = new DefaultApi();
    let apiHealthGet = bindNodeCallback(defaultApi.apiHealthGet);
    return apiHealthGet();
}