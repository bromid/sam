import { takeLatest, takeEvery } from 'redux-saga';
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
    DELETE_APPLICATION_REQUEST,
    DELETE_APPLICATION_RESPONSE,
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

const deleteApplication = createFetchSaga({
    apiCall: API.deleteApplication,
    responseKey: DELETE_APPLICATION_RESPONSE,
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
        const { id } = action.request;
        yield put(showErrorNotification(`Failed to update application ${id}`, action.payload));
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

function* deleteApplicationResponse(action) {
    const { id } = action.request;
    if (!action.error) {
        yield put(showNotification(`Deleted application ${id}`));
        browserHistory.push('/application');
    } else {
        yield put(showErrorNotification(`Failed to delete application ${id}`, action.payload));
    }
}

function* fetchApplicationFork(action) {
    yield fork(fetchApplication, action);
    yield fork(fetchApplicationDeployments, action);
}

export default function* applicationSagas() {
    yield fork(takeLatest, FETCH_APPLICATION_REQUEST, fetchApplicationFork);
    yield fork(takeLatest, FETCH_APPLICATION_LIST_REQUEST, fetchApplicationList);
    yield fork(takeEvery, PATCH_APPLICATION_REQUEST, patchApplication);
    yield fork(takeEvery, PATCH_APPLICATION_RESPONSE, patchApplicationResponse);
    yield fork(takeEvery, CREATE_APPLICATION_REQUEST, createApplication);
    yield fork(takeEvery, CREATE_APPLICATION_RESPONSE, createApplicationResponse);
    yield fork(takeEvery, DELETE_APPLICATION_REQUEST, deleteApplication);
    yield fork(takeEvery, DELETE_APPLICATION_RESPONSE, deleteApplicationResponse);
}
