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
