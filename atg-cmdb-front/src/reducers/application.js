import * as Constants from '../constants';
import createFetchReducers from '../createFetchReducers';

export default createFetchReducers({
    resourceName: 'application',
    requestKey: Constants.FETCH_APPLICATION_REQUEST,
    receiveKey: Constants.FETCH_APPLICATION_RESPONSE,
});
