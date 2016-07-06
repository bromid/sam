import * as Constants from '../constants';
import createFetchReducers from '../createFetchReducers';

export default createFetchReducers({
    resourceName: 'info',
    requestKey: Constants.FETCH_INFO_REQUEST,
    receiveKey: Constants.FETCH_INFO_RESPONSE,
});
