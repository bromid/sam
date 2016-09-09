import { takeLatest, takeEvery } from 'redux-saga';
import { fork, put } from 'redux-saga/effects';
import { browserHistory } from 'react-router';
import * as API from '../api';
import createFetchSaga from './helpers/createFetchSaga';
import * as assetActions from '../actions/assetActions';
import { showNotification, showErrorNotification } from '../actions/notificationActions';
import {
    FETCH_ASSET_LIST_REQUEST,
    FETCH_ASSET_LIST_RESPONSE,
    FETCH_ASSET_REQUEST,
    FETCH_ASSET_RESPONSE,
    PATCH_ASSET_REQUEST,
    PATCH_ASSET_RESPONSE,
    CREATE_ASSET_REQUEST,
    CREATE_ASSET_RESPONSE,
    DELETE_ASSET_REQUEST,
    DELETE_ASSET_RESPONSE,
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

const createAsset = createFetchSaga({
    apiCall: API.createAsset,
    responseKey: CREATE_ASSET_RESPONSE,
});

const deleteAsset = createFetchSaga({
    apiCall: API.deleteAsset,
    responseKey: DELETE_ASSET_RESPONSE,
});

function* patchAssetResponse(action) {
    if (!action.error) {
        const { id, name } = action.payload;
        yield put(showNotification(`Updated asset ${name}`));
        yield put(assetActions.fetchAsset(id));
    } else {
        yield put(showErrorNotification('Failed to update asset', action.payload));
    }
}

function* createAssetResponse(action) {
    if (!action.error) {
        const { id, name } = action.payload;
        yield put(showNotification(`Created asset ${name}`));
        browserHistory.push(`/asset/${id}`);
    } else {
        yield put(showErrorNotification('Failed to create asset', action.payload));
    }
}

function* deleteAssetResponse(action) {
    const { id } = action.request;
    if (!action.error) {
        yield put(showNotification(`Deleted asset ${id}`));
        browserHistory.push('/asset');
    } else {
        yield put(showErrorNotification(`Failed to delete asset ${id}`, action.payload));
    }
}

export default function* assetSagas() {
    yield fork(takeLatest, FETCH_ASSET_REQUEST, fetchAsset);
    yield fork(takeLatest, FETCH_ASSET_LIST_REQUEST, fetchAssetList);
    yield fork(takeEvery, PATCH_ASSET_REQUEST, patchAsset);
    yield fork(takeEvery, PATCH_ASSET_RESPONSE, patchAssetResponse);
    yield fork(takeEvery, CREATE_ASSET_REQUEST, createAsset);
    yield fork(takeEvery, CREATE_ASSET_RESPONSE, createAssetResponse);
    yield fork(takeEvery, DELETE_ASSET_REQUEST, deleteAsset);
    yield fork(takeEvery, DELETE_ASSET_RESPONSE, deleteAssetResponse);
}
