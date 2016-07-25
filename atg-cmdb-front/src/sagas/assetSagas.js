import { takeLatest } from 'redux-saga';
import { call, put, fork } from 'redux-saga/effects';
import * as API from '../api';
import {
    FETCH_ASSET_LIST_REQUEST,
    FETCH_ASSET_LIST_RESPONSE,
    FETCH_ASSET_REQUEST,
    FETCH_ASSET_RESPONSE,
    PATCH_ASSET_REQUEST,
    PATCH_ASSET_RESPONSE,
} from '../constants';

function* fetchAssetList() {
    const response = yield call(API.fetchAssetList);
    yield put({
        type: FETCH_ASSET_LIST_RESPONSE,
        payload: response.data,
    });
}

function* fetchAsset(action) {
    const response = yield call(API.fetchAsset, action.payload.id);
    yield put({
        type: FETCH_ASSET_RESPONSE,
        payload: response.data,
    });
}

function* patchAsset({ payload: { id, data, options } }) {
    try {
        const response = yield call(API.patchAsset, id, data, options);
        yield put({
            type: PATCH_ASSET_RESPONSE,
            payload: response.data,
        });
    } catch (error) {
        yield put({
            type: PATCH_ASSET_RESPONSE,
            error: true,
            payload: { status: error },
        });
    }
}

function* patchAssetResponse(action) {
    if (!action.error) {
        yield fork(fetchAsset, action);
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
