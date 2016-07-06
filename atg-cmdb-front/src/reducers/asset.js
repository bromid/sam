import * as Constants from '../constants';
import createFetchReducers from '../createFetchReducers';

export default createFetchReducers({
    resourceName: 'asset',
    requestKey: Constants.FETCH_ASSET_REQUEST,
    receiveKey: Constants.FETCH_ASSET_RESPONSE,
});
