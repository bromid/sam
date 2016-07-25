import { takeLatest } from 'redux-saga';
import { take, call, put, fork } from 'redux-saga/effects';
import * as API from '../api';
import {
    FETCH_APPLICATION_LIST_REQUEST,
    FETCH_APPLICATION_LIST_RESPONSE,
    FETCH_APPLICATION_REQUEST,
    FETCH_APPLICATION_RESPONSE,
    PATCH_APPLICATION_REQUEST,
    PATCH_APPLICATION_RESPONSE,
    FETCH_APPLICATION_DEPLOYMENTS_RESPONSE,
} from '../constants';

function* fetchApplicationList() {
    const response = yield call(API.fetchApplicationList);
    yield put({
        type: FETCH_APPLICATION_LIST_RESPONSE,
        payload: response.data,
    });
}

function* fetchApplication(applicationId) {
    const response = yield call(API.fetchApplication, applicationId);
    yield put({
        type: FETCH_APPLICATION_RESPONSE,
        payload: response.data,
    });
}

function* patchApplication({ payload: { id, data, options } }) {
    try {
        const response = yield call(API.patchApplication, id, data, options);
        yield put({
            type: PATCH_APPLICATION_RESPONSE,
            payload: response.data,
        });
    } catch (error) {
        yield put({
            type: PATCH_APPLICATION_RESPONSE,
            error: true,
            payload: { status: error },
        });
    }
}

function* fetchApplicationDeployments(applicationId) {
    const response = yield call(API.fetchApplicationDeployments, applicationId);
    yield put({
        type: FETCH_APPLICATION_DEPLOYMENTS_RESPONSE,
        payload: response.data,
    });
}

/** Watch-sagas start **/

export function* watchFetchApplicationList() {
    yield* takeLatest(FETCH_APPLICATION_LIST_REQUEST, fetchApplicationList);
}

export function* watchFetchApplication() {
    while (true) {
        const action = yield take(FETCH_APPLICATION_REQUEST);
        yield fork(fetchApplication, action.payload);
        yield fork(fetchApplicationDeployments, action.payload);
    }
}

export function* watchPatchApplicationRequeset() {
    yield* takeLatest(PATCH_APPLICATION_REQUEST, patchApplication);
}

export function* watchPatchApplicationResponse() {
    while (true) {
        const action = yield take(PATCH_APPLICATION_RESPONSE);

        if (!action.error) {
            yield fork(fetchApplication, action.payload.id);
        }
    }
}
export default function* applicationSagas() {
    yield fork(watchFetchApplication);
    yield fork(watchFetchApplicationList);
    yield fork(watchPatchApplicationResponse);
    yield fork(watchPatchApplicationRequeset);
}
