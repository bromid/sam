import { takeLatest } from 'redux-saga';
import { fork } from 'redux-saga/effects';
import * as API from '../api';
import createFetchSaga from './helpers/createFetchSaga';
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
    paramSelector(action) {
        return action.payload;
    },
});

const fetchServer = createFetchSaga({
    apiCall: API.fetchServer,
    responseKey: FETCH_SERVER_RESPONSE,
    paramSelector(action) {
        return action.payload;
    },
});

const patchServer = createFetchSaga({
    apiCall: API.patchServer,
    responseKey: PATCH_SERVER_RESPONSE,
    paramSelector(action) {
        const { payload: { params, data, options } } = action;
        return [params, data, options];
    },
});

function* patchServerResponse(action) {
    if (!action.error) {
        yield fork(fetchServer, action);
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
