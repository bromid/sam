import * as Constants from '../constants';
import createFetchReducers from '../createFetchReducers';

export default createFetchReducers({
    resourceName: 'applicationList',
    requestKey: Constants.FETCH_APPLICATION_LIST_REQUEST,
    receiveKey: Constants.FETCH_APPLICATION_LIST_RESPONSE,
});
