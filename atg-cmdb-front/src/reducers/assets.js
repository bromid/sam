import * as Constants from '../constants';
import createFetchReducers from '../createFetchReducers';

export default createFetchReducers({
    resourceName: 'assetList',
    requestKey: Constants.FETCH_ASSET_LIST_REQUEST,
    receiveKey: Constants.FETCH_ASSET_LIST_RESPONSE,
});
