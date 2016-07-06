import * as Constants from '../constants';
import createFetchReducers from '../createFetchReducers';

export default createFetchReducers({
    resourceName: 'groupList',
    requestKey: Constants.FETCH_GROUP_LIST_REQUEST,
    receiveKey: Constants.FETCH_GROUP_LIST_RESPONSE,
});
