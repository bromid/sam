import { takeLatest } from 'redux-saga';
import { fork, put } from 'redux-saga/effects';
import { browserHistory } from 'react-router';
import * as API from '../api';
import createFetchSaga from './helpers/createFetchSaga';
import * as applicationActions from '../actions/applicationActions';
import { showNotification, showErrorNotification } from '../actions/notificationActions';
import {
    FETCH_APPLICATION_LIST_REQUEST,
    FETCH_APPLICATION_LIST_RESPONSE,
    FETCH_APPLICATION_REQUEST,
    FETCH_APPLICATION_RESPONSE,
    PATCH_APPLICATION_REQUEST,
    PATCH_APPLICATION_RESPONSE,
    CREATE_APPLICATION_REQUEST,
    CREATE_APPLICATION_RESPONSE,
    FETCH_APPLICATION_DEPLOYMENTS_RESPONSE,
} from '../constants';

const fetchApplicationList = createFetchSaga({
    apiCall: API.fetchApplicationList,
    responseKey: FETCH_APPLICATION_LIST_RESPONSE,
});

const fetchApplication = createFetchSaga({
    apiCall: API.fetchApplication,
    responseKey: FETCH_APPLICATION_RESPONSE,
    paramSelector(action) {
        return action.payload.id;
    },
});

const patchApplication = createFetchSaga({
    apiCall: API.patchApplication,
    responseKey: PATCH_APPLICATION_RESPONSE,
});

const createApplication = createFetchSaga({
    apiCall: API.createApplication,
    responseKey: CREATE_APPLICATION_RESPONSE,
});

const fetchApplicationDeployments = createFetchSaga({
    apiCall: API.fetchApplicationDeployments,
    responseKey: FETCH_APPLICATION_DEPLOYMENTS_RESPONSE,
    paramSelector(action) {
        return action.payload.id;
    },
});

function* patchApplicationResponse(action) {
    if (!action.error) {
        const { id, name } = action.payload;
        yield put(showNotification(`Updated application ${name}`));
        yield put(applicationActions.fetchApplication(id));
    } else {
        yield put(showErrorNotification('Failed to update application', action.payload));
    }
}

function* createApplicationResponse(action) {
    if (!action.error) {
        const { id, name } = action.payload;
        yield put(showNotification(`Created application ${name}`));
        browserHistory.push(`/application/${id}`);
    } else {
        yield put(showErrorNotification('Failed to create application', action.payload));
    }
}

function* fetchApplicationFork(action) {
    yield fork(fetchApplication, action);
    yield fork(fetchApplicationDeployments, action);
}

/** Watch-sagas start **/

export function* watchFetchApplicationList() {
    yield* takeLatest(FETCH_APPLICATION_LIST_REQUEST, fetchApplicationList);
}

export function* watchFetchApplication() {
    yield* takeLatest(FETCH_APPLICATION_REQUEST, fetchApplicationFork);
}

export function* watchPatchApplicationRequest() {
    yield* takeLatest(PATCH_APPLICATION_REQUEST, patchApplication);
}

export function* watchPatchApplicationResponse() {
    yield* takeLatest(PATCH_APPLICATION_RESPONSE, patchApplicationResponse);
}

export function* watchCreateApplicationRequest() {
    yield* takeLatest(CREATE_APPLICATION_REQUEST, createApplication);
}

export function* watchCreateApplicationResponse() {
    yield* takeLatest(CREATE_APPLICATION_RESPONSE, createApplicationResponse);
}

export default function* applicationSagas() {
    yield fork(watchFetchApplication);
    yield fork(watchFetchApplicationList);
    yield fork(watchPatchApplicationResponse);
    yield fork(watchPatchApplicationRequest);
    yield fork(watchCreateApplicationRequest);
    yield fork(watchCreateApplicationResponse);
}
