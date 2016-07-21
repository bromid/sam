import createFetchActions from '../createFetchActions';
import * as Constants from '../constants';
import * as API from '../api';

export const fetchAssetList = createFetchActions({
    apiCall: API.fetchAssetList,
    requestKey: Constants.FETCH_ASSET_LIST_REQUEST,
    receiveKey: Constants.FETCH_ASSET_LIST_RESPONSE,
});

export const fetchAsset = createFetchActions({
    apiCall: API.fetchAsset,
    requestKey: Constants.FETCH_ASSET_REQUEST,
    receiveKey: Constants.FETCH_ASSET_RESPONSE,
});

export const patchAsset = createFetchActions({
    apiCall: API.patchAsset,
    requestKey: Constants.PATCH_ASSET_REQUEST,
    receiveKey: Constants.PATCH_ASSET_RESPONSE,
    payloadTransform: (data, response) => ({
        ...data,
        etag: response.headers.get('etag'),
    }),
});
