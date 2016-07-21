import * as Constants from '../constants';
import createFetchReducers from '../createFetchReducers';

const asset = createFetchReducers({
    resourceName: 'asset',
    requestKey: Constants.FETCH_ASSET_REQUEST,
    receiveKey: Constants.FETCH_ASSET_RESPONSE,
});

const assetPatchResult = createFetchReducers({
    resourceName: 'assetPatchResult',
    requestKey: Constants.PATCH_ASSET_REQUEST,
    receiveKey: Constants.PATCH_ASSET_RESPONSE,
});

const assetList = createFetchReducers({
    resourceName: 'assetList',
    requestKey: Constants.FETCH_ASSET_LIST_REQUEST,
    receiveKey: Constants.FETCH_ASSET_LIST_RESPONSE,
});

export default {
    ...asset,
    ...assetPatchResult,
    ...assetList,
};
