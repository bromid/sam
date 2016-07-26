import { takeLatest } from 'redux-saga';
import { fork } from 'redux-saga/effects';
import * as API from '../api';
import createFetchSaga from './helpers/createFetchSaga';
import {
    FETCH_GROUP_LIST_REQUEST,
    FETCH_GROUP_LIST_RESPONSE,
    FETCH_GROUP_REQUEST,
    FETCH_GROUP_RESPONSE,
    FETCH_GROUP_TAG_REQUEST,
    FETCH_GROUP_TAG_RESPONSE,
    PATCH_GROUP_REQUEST,
    PATCH_GROUP_RESPONSE,
    CREATE_GROUP_REQUEST,
    CREATE_GROUP_RESPONSE,
} from '../constants';

const fetchGroupList = createFetchSaga({
    apiCall: API.fetchGroupList,
    responseKey: FETCH_GROUP_LIST_RESPONSE,
});

const fetchGroup = createFetchSaga({
    apiCall: API.fetchGroup,
    responseKey: FETCH_GROUP_RESPONSE,
    paramSelector(action) {
        return action.payload.id;
    },
});

const fetchGroupTags = createFetchSaga({
    apiCall: API.fetchGroupTags,
    responseKey: FETCH_GROUP_TAG_RESPONSE,
});

const patchGroup = createFetchSaga({
    apiCall: API.patchGroup,
    responseKey: PATCH_GROUP_RESPONSE,
});

const createGroup = createFetchSaga({
    apiCall: API.createGroup,
    responseKey: CREATE_GROUP_RESPONSE,
});

function* patchGroupResponse(action) {
    if (!action.error) {
        yield fork(fetchGroup, action);
    }
}

function* createGroupResponse(action) {
    if (!action.error) {
        yield fork(fetchGroup, action);
    }
}

/** Watch-sagas start **/

export function* watchFetchGroupList() {
    yield* takeLatest(FETCH_GROUP_LIST_REQUEST, fetchGroupList);
}

export function* watchFetchGroup() {
    yield* takeLatest(FETCH_GROUP_REQUEST, fetchGroup);
}

export function* watchFetchGroupTags() {
    yield* takeLatest(FETCH_GROUP_TAG_REQUEST, fetchGroupTags);
}

export function* watchPatchGroupRequest() {
    yield* takeLatest(PATCH_GROUP_REQUEST, patchGroup);
}

export function* watchPatchGroupResponse() {
    yield* takeLatest(PATCH_GROUP_RESPONSE, patchGroupResponse);
}

export function* watchCreateGroupRequest() {
    yield* takeLatest(CREATE_GROUP_REQUEST, createGroup);
}

export function* watchCreateGroupResponse() {
    yield* takeLatest(CREATE_GROUP_RESPONSE, createGroupResponse);
}

export default function* groupSagas() {
    yield fork(watchFetchGroup);
    yield fork(watchFetchGroupList);
    yield fork(watchFetchGroupTags);
    yield fork(watchPatchGroupRequest);
    yield fork(watchPatchGroupResponse);
    yield fork(watchCreateGroupRequest);
    yield fork(watchCreateGroupResponse);
}
