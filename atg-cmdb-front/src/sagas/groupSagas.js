import { takeLatest } from 'redux-saga';
import { fork, put } from 'redux-saga/effects';
import { browserHistory } from 'react-router';
import * as API from '../api';
import createFetchSaga from './helpers/createFetchSaga';
import * as groupActions from '../actions/groupActions';
import { showNotification, showErrorNotification } from '../actions/notificationActions';
import {
    FETCH_GROUP_LIST_REQUEST,
    FETCH_GROUP_LIST_RESPONSE,
    FETCH_GROUP_REQUEST,
    FETCH_GROUP_RESPONSE,
    FETCH_GROUP_TAG_REQUEST,
    FETCH_GROUP_TAG_RESPONSE,
    FETCH_GROUP_ID_REQUEST,
    FETCH_GROUP_ID_RESPONSE,
    PATCH_GROUP_REQUEST,
    PATCH_GROUP_RESPONSE,
    CREATE_GROUP_REQUEST,
    CREATE_GROUP_RESPONSE,
    ADD_SUBGROUP_REQUEST,
    ADD_SUBGROUP_RESPONSE,
    REMOVE_SUBGROUP_REQUEST,
    REMOVE_SUBGROUP_RESPONSE,
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

const fetchGroupIds = createFetchSaga({
    apiCall: API.fetchGroupIds,
    responseKey: FETCH_GROUP_ID_RESPONSE,
});

const patchGroup = createFetchSaga({
    apiCall: API.patchGroup,
    responseKey: PATCH_GROUP_RESPONSE,
});

const createGroup = createFetchSaga({
    apiCall: API.createGroup,
    responseKey: CREATE_GROUP_RESPONSE,
});

const addSubgroup = createFetchSaga({
    apiCall: API.addSubgroup,
    responseKey: ADD_SUBGROUP_RESPONSE,
});

const removeSubgroup = createFetchSaga({
    apiCall: API.removeSubgroup,
    responseKey: REMOVE_SUBGROUP_RESPONSE,
});

function* patchGroupResponse(action) {
    if (!action.error) {
        yield fork(fetchGroup, action);
        const { name } = action.payload;
        yield put(showNotification(`Updated group ${name}`));
    } else {
        yield put(showErrorNotification('Failed to update group', action.payload));
    }
}

function* createGroupResponse(action) {
    if (!action.error) {
        const { id, name } = action.payload;
        yield put(showNotification(`Created group ${name}`));
        browserHistory.push(`/group/${id}`);
    } else {
        yield put(showErrorNotification('Failed to create group', action.payload));
    }
}

function* addSubgroupResponse(action) {
    const { groupId, subGroupId } = action.request;
    if (!action.error) {
        const message = `Added ${subGroupId} as a sub group to ${groupId}`;
        yield put(showNotification(message));
        yield put(groupActions.fetchGroup(groupId));
    } else {
        const message = `Failed to add ${subGroupId} as a sub group to ${groupId}`;
        yield put(showErrorNotification(message, action.payload));
    }
}

function* removeSubgroupResponse(action) {
    const { groupId, subGroupId } = action.request;
    if (!action.error) {
        const message = `Removed sub group ${subGroupId} from ${groupId}`;
        yield put(showNotification(message));
        yield put(groupActions.fetchGroup(groupId));
    } else {
        const message = `Failed to remove ${subGroupId} as a sub group of ${groupId}`;
        yield put(showErrorNotification(message, action.payload));
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

export function* watchFetchGroupIds() {
    yield* takeLatest(FETCH_GROUP_ID_REQUEST, fetchGroupIds);
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

export function* watchAddSubgroupRequest() {
    yield* takeLatest(ADD_SUBGROUP_REQUEST, addSubgroup);
}

export function* watchAddSubgroupResponse() {
    yield* takeLatest(ADD_SUBGROUP_RESPONSE, addSubgroupResponse);
}

export function* watchRemoveSubgroupRequest() {
    yield* takeLatest(REMOVE_SUBGROUP_REQUEST, removeSubgroup);
}

export function* watchRemoveSubgroupResponse() {
    yield* takeLatest(REMOVE_SUBGROUP_RESPONSE, removeSubgroupResponse);
}

export default function* groupSagas() {
    yield fork(watchFetchGroup);
    yield fork(watchFetchGroupList);
    yield fork(watchFetchGroupTags);
    yield fork(watchFetchGroupIds);
    yield fork(watchPatchGroupRequest);
    yield fork(watchPatchGroupResponse);
    yield fork(watchCreateGroupRequest);
    yield fork(watchCreateGroupResponse);
    yield fork(watchAddSubgroupRequest);
    yield fork(watchAddSubgroupResponse);
    yield fork(watchRemoveSubgroupRequest);
    yield fork(watchRemoveSubgroupResponse);
}
