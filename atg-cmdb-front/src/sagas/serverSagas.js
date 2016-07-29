import { takeLatest } from 'redux-saga';
import { fork, put } from 'redux-saga/effects';
import * as API from '../api';
import { serverName } from '../components/Server';
import createFetchSaga from './helpers/createFetchSaga';
import { showNotification, showErrorNotification } from '../actions/notificationActions';
import {
    FETCH_SERVER_LIST_REQUEST,
    FETCH_SERVER_LIST_RESPONSE,
    FETCH_SERVER_REQUEST,
    FETCH_SERVER_RESPONSE,
    PATCH_SERVER_REQUEST,
    PATCH_SERVER_RESPONSE,
} from '../constants';

const fetchServerList = createFetchSaga({
    apiCall: API.fetchServerList,
    responseKey: FETCH_SERVER_LIST_RESPONSE,
});

const fetchServer = createFetchSaga({
    apiCall: API.fetchServer,
    responseKey: FETCH_SERVER_RESPONSE,
});

const patchServer = createFetchSaga({
    apiCall: API.patchServer,
    responseKey: PATCH_SERVER_RESPONSE,
});

function* patchServerResponse(action) {
    if (!action.error) {
        yield fork(fetchServer, action);
        yield put(showNotification(`Updated server ${serverName(action.payload)}`));
    } else {
        yield put(showErrorNotification('Failed to update server', action.payload));
    }
}

/** Watch-sagas start **/

export function* watchFetchServerList() {
    yield* takeLatest(FETCH_SERVER_LIST_REQUEST, fetchServerList);
}

export function* watchFetchServer() {
    yield* takeLatest(FETCH_SERVER_REQUEST, fetchServer);
}

export function* watchPatchServerRequest() {
    yield* takeLatest(PATCH_SERVER_REQUEST, patchServer);
}

export function* watchPatchServerResponse() {
    yield* takeLatest(PATCH_SERVER_RESPONSE, patchServerResponse);
}

export default function* serverSagas() {
    yield fork(watchFetchServer);
    yield fork(watchFetchServerList);
    yield fork(watchPatchServerRequest);
    yield fork(watchPatchServerResponse);
}
