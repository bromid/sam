import * as Constants from '../constants';
import createFetchReducers from '../createFetchReducers';

export default createFetchReducers({
    resourceName: 'serverList',
    requestKey: Constants.FETCH_SERVER_LIST_REQUEST,
    receiveKey: Constants.FETCH_SERVER_LIST_RESPONSE,
});
