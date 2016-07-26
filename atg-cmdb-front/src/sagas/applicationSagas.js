import { takeLatest } from 'redux-saga';
import { take, fork } from 'redux-saga/effects';
import * as API from '../api';
import createFetchSaga from './helpers/createFetchSaga';
import {
    FETCH_APPLICATION_LIST_REQUEST,
    FETCH_APPLICATION_LIST_RESPONSE,
    FETCH_APPLICATION_REQUEST,
    FETCH_APPLICATION_RESPONSE,
    PATCH_APPLICATION_REQUEST,
    PATCH_APPLICATION_RESPONSE,
    FETCH_APPLICATION_DEPLOYMENTS_RESPONSE,
} from '../constants';

const fetchApplicationList = createFetchSaga({
    apiCall: API.fetchApplicationList,
    responseKey: FETCH_APPLICATION_LIST_RESPONSE,
});

const fetchApplication = createFetchSaga({
    apiCall: API.fetchApplication,
    responseKey: FETCH_APPLICATION_RESPONSE,
});

const patchApplication = createFetchSaga({
    apiCall: API.patchApplication,
    paramSelector(action) {
        const { payload: { id, data, options } } = action;
        return [id, data, options];
    },
    responseKey: PATCH_APPLICATION_RESPONSE,
});

const fetchApplicationDeployments = createFetchSaga({
    apiCall: API.fetchApplicationDeployments,
    responseKey: FETCH_APPLICATION_DEPLOYMENTS_RESPONSE,
});


function* patchApplicationResponse(action) {
    if (!action.error) {
        yield fork(fetchApplication, action.payload.id);
    }
}

/** Watch-sagas start **/

export function* watchFetchApplicationList() {
    yield* takeLatest(FETCH_APPLICATION_LIST_REQUEST, fetchApplicationList);
}

export function* watchFetchApplication() {
    while (true) { // eslint-disable-line no-constant-condition
        const action = yield take(FETCH_APPLICATION_REQUEST);
        yield fork(fetchApplication, action.payload);
        yield fork(fetchApplicationDeployments, action.payload);
    }
}

export function* watchPatchApplicationRequest() {
    yield* takeLatest(PATCH_APPLICATION_REQUEST, patchApplication);
}

export function* watchPatchApplicationResponse() {
    yield* takeLatest(PATCH_APPLICATION_RESPONSE, patchApplicationResponse);
}

export default function* applicationSagas() {
    yield fork(watchFetchApplication);
    yield fork(watchFetchApplicationList);
    yield fork(watchPatchApplicationResponse);
    yield fork(watchPatchApplicationRequest);
}
