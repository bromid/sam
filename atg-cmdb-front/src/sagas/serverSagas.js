import { takeLatest, takeEvery } from 'redux-saga';
import { fork, put } from 'redux-saga/effects';
import * as API from '../api';
import { serverName } from '../components/ServerList';
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

export default function* serverSagas() {
    yield fork(takeLatest, FETCH_SERVER_REQUEST, fetchServer);
    yield fork(takeLatest, FETCH_SERVER_LIST_REQUEST, fetchServerList);
    yield fork(takeEvery, PATCH_SERVER_REQUEST, patchServer);
    yield fork(takeEvery, PATCH_SERVER_RESPONSE, patchServerResponse);
}
