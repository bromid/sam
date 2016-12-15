import { takeLatest, takeEvery } from 'redux-saga';
import { fork, put, call } from 'redux-saga/effects';
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
    FETCH_GROUP_DEPLOYMENTS_REQUEST,
    FETCH_GROUP_DEPLOYMENTS_RESPONSE,
    FETCH_GROUP_ID_RESPONSE,
    PATCH_GROUP_REQUEST,
    PATCH_GROUP_RESPONSE,
    CREATE_GROUP_REQUEST,
    CREATE_GROUP_RESPONSE,
    DELETE_GROUP_REQUEST,
    DELETE_GROUP_RESPONSE,
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

const fetchGroupDeployments = createFetchSaga({
    apiCall: API.fetchApplicationDeployments,
    responseKey: FETCH_GROUP_DEPLOYMENTS_RESPONSE,
});

const patchGroup = createFetchSaga({
    apiCall: API.patchGroup,
    responseKey: PATCH_GROUP_RESPONSE,
});

const createGroup = createFetchSaga({
    apiCall: API.createGroup,
    responseKey: CREATE_GROUP_RESPONSE,
});

const deleteGroup = createFetchSaga({
    apiCall: API.deleteGroup,
    responseKey: DELETE_GROUP_RESPONSE,
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
        const { id, name } = action.payload;
        yield put(showNotification(`Updated group ${name}`));
        yield put(groupActions.fetchGroup(id));
    } else {
        yield put(showErrorNotification('Failed to update group', action.payload));
    }
}

function* createGroupResponse(action) {
    if (!action.error) {
        const { name } = action.payload;
        yield put(showNotification(`Created group ${name}`));
        yield call(action.callback);
    } else {
        yield put(showErrorNotification('Failed to create group', action.payload));
    }
}

function* deleteGroupResponse(action) {
    const { id } = action.request;
    if (!action.error) {
        yield put(showNotification(`Deleted group ${id}`));
        yield call(action.callback);
    } else {
        yield put(showErrorNotification(`Failed to delete group ${id}`, action.payload));
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

export default function* groupSagas() {
    yield fork(takeLatest, FETCH_GROUP_REQUEST, fetchGroup);
    yield fork(takeLatest, FETCH_GROUP_LIST_REQUEST, fetchGroupList);
    yield fork(takeLatest, FETCH_GROUP_TAG_REQUEST, fetchGroupTags);
    yield fork(takeLatest, FETCH_GROUP_ID_REQUEST, fetchGroupIds);
    yield fork(takeEvery, FETCH_GROUP_DEPLOYMENTS_REQUEST, fetchGroupDeployments);
    yield fork(takeEvery, PATCH_GROUP_REQUEST, patchGroup);
    yield fork(takeEvery, PATCH_GROUP_RESPONSE, patchGroupResponse);
    yield fork(takeEvery, CREATE_GROUP_REQUEST, createGroup);
    yield fork(takeEvery, CREATE_GROUP_RESPONSE, createGroupResponse);
    yield fork(takeEvery, DELETE_GROUP_REQUEST, deleteGroup);
    yield fork(takeEvery, DELETE_GROUP_RESPONSE, deleteGroupResponse);
    yield fork(takeEvery, ADD_SUBGROUP_REQUEST, addSubgroup);
    yield fork(takeEvery, ADD_SUBGROUP_RESPONSE, addSubgroupResponse);
    yield fork(takeEvery, REMOVE_SUBGROUP_REQUEST, removeSubgroup);
    yield fork(takeEvery, REMOVE_SUBGROUP_RESPONSE, removeSubgroupResponse);
}
