import { takeLatest } from 'redux-saga';
import { fork, put } from 'redux-saga/effects';
import * as API from '../api';
import createFetchSaga from './helpers/createFetchSaga';
import { showNotification, showErrorNotification } from '../actions/notificationActions';
import {
    FETCH_ASSET_LIST_REQUEST,
    FETCH_ASSET_LIST_RESPONSE,
    FETCH_ASSET_REQUEST,
    FETCH_ASSET_RESPONSE,
    PATCH_ASSET_REQUEST,
    PATCH_ASSET_RESPONSE,
} from '../constants';

const fetchAssetList = createFetchSaga({
    apiCall: API.fetchAssetList,
    responseKey: FETCH_ASSET_LIST_RESPONSE,
});

const fetchAsset = createFetchSaga({
    apiCall: API.fetchAsset,
    responseKey: FETCH_ASSET_RESPONSE,
    paramSelector(action) {
        return action.payload.id;
    },
});

const patchAsset = createFetchSaga({
    apiCall: API.patchAsset,
    responseKey: PATCH_ASSET_RESPONSE,
});

function* patchAssetResponse(action) {
    if (!action.error) {
        yield fork(fetchAsset, action);
        const { name } = action.payload;
        yield put(showNotification(`Updated asset ${name}`));
    } else {
        yield put(showErrorNotification('Failed to update asset', action.payload));
    }
}

/** Watch-sagas start **/

export function* watchFetchAssetList() {
    yield* takeLatest(FETCH_ASSET_LIST_REQUEST, fetchAssetList);
}

export function* watchFetchAsset() {
    yield* takeLatest(FETCH_ASSET_REQUEST, fetchAsset);
}

export function* watchPatchAssetRequest() {
    yield* takeLatest(PATCH_ASSET_REQUEST, patchAsset);
}

export function* watchPatchAssetResponse() {
    yield* takeLatest(PATCH_ASSET_RESPONSE, patchAssetResponse);
}

export default function* assetSagas() {
    yield fork(watchFetchAsset);
    yield fork(watchFetchAssetList);
    yield fork(watchPatchAssetRequest);
    yield fork(watchPatchAssetResponse);
}
