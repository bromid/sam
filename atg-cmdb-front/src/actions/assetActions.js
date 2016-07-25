import {
    FETCH_ASSET_LIST_REQUEST,
    FETCH_ASSET_REQUEST,
    PATCH_ASSET_REQUEST,
} from '../constants';

export const fetchAssetList = () => ({
    type: FETCH_ASSET_LIST_REQUEST,
});

export const fetchAsset = (assetId) => ({
    type: FETCH_ASSET_REQUEST,
    payload: { id: assetId },
});

export const patchAsset = (id, data, options) => ({
    type: PATCH_ASSET_REQUEST,
    payload: { id, data, options },
});
