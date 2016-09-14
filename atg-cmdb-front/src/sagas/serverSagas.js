import { takeLatest, takeEvery } from 'redux-saga';
import { fork, put } from 'redux-saga/effects';
import { browserHistory } from 'react-router';
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
    CREATE_SERVER_REQUEST,
    CREATE_SERVER_RESPONSE,
    DELETE_SERVER_REQUEST,
    DELETE_SERVER_RESPONSE,
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

const createServer = createFetchSaga({
    apiCall: API.createServer,
    responseKey: CREATE_SERVER_RESPONSE,
});

const deleteServer = createFetchSaga({
    apiCall: API.deleteServer,
    responseKey: DELETE_SERVER_RESPONSE,
});

function* patchServerResponse(action) {
    const name = serverName(action.request);

    if (!action.error) {
        yield fork(fetchServer, action);
        yield put(showNotification(`Updated server ${name}`));
    } else {
        yield put(showErrorNotification(`Failed to update server ${name}`, action.payload));
    }
}

function* createServerResponse(action) {
    const name = serverName(action.request);

    if (!action.error) {
        const { hostname, environment } = action.request;
        yield put(showNotification(`Created server ${name}`));
        browserHistory.push(`/server/${environment}/${hostname}`);
    } else {
        yield put(showErrorNotification(`Failed to create server ${name}`, action.payload));
    }
}

function* deleteServerResponse(action) {
    const name = serverName(action.request);

    if (!action.error) {
        yield put(showNotification(`Deleted server ${name}`));
        browserHistory.push('/server');
    } else {
        yield put(showErrorNotification(`Failed to delete server ${name}`, action.payload));
    }
}

export default function* serverSagas() {
    yield fork(takeLatest, FETCH_SERVER_REQUEST, fetchServer);
    yield fork(takeLatest, FETCH_SERVER_LIST_REQUEST, fetchServerList);
    yield fork(takeEvery, PATCH_SERVER_REQUEST, patchServer);
    yield fork(takeEvery, PATCH_SERVER_RESPONSE, patchServerResponse);
    yield fork(takeEvery, CREATE_SERVER_REQUEST, createServer);
    yield fork(takeEvery, CREATE_SERVER_RESPONSE, createServerResponse);
    yield fork(takeEvery, DELETE_SERVER_REQUEST, deleteServer);
    yield fork(takeEvery, DELETE_SERVER_RESPONSE, deleteServerResponse);
}
