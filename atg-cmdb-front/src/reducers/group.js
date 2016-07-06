import * as Constants from '../constants';
import createFetchReducers from '../createFetchReducers';

export default createFetchReducers({
    resourceName: 'group',
    requestKey: Constants.FETCH_GROUP_REQUEST,
    receiveKey: Constants.FETCH_GROUP_RESPONSE,
});
