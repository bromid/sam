import * as Constants from '../constants';
import createFetchReducers from '../createFetchReducers';

export default createFetchReducers({
    resourceName: 'server',
    requestKey: Constants.FETCH_SERVER_REQUEST,
    receiveKey: Constants.FETCH_SERVER_RESPONSE,
});
