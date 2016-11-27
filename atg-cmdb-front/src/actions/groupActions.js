import {
    FETCH_GROUP_LIST_REQUEST,
    FETCH_GROUP_REQUEST,
    FETCH_GROUP_TAG_REQUEST,
    FETCH_GROUP_ID_REQUEST,
    FETCH_GROUP_DEPLOYMENTS_REQUEST,
    PATCH_GROUP_REQUEST,
    CREATE_GROUP_REQUEST,
    DELETE_GROUP_REQUEST,
    ADD_SUBGROUP_REQUEST,
    REMOVE_SUBGROUP_REQUEST,
} from '../constants';

export const fetchGroupList = (tags) => ({
    type: FETCH_GROUP_LIST_REQUEST,
    payload: { tags },
});

export const fetchGroup = (groupId) => ({
    type: FETCH_GROUP_REQUEST,
    payload: { id: groupId },
});

export const fetchGroupTags = () => ({
    type: FETCH_GROUP_TAG_REQUEST,
});

export const fetchGroupIds = () => ({
    type: FETCH_GROUP_ID_REQUEST,
});

export const fetchGroupDeployments = (applicationId) => ({
    type: FETCH_GROUP_DEPLOYMENTS_REQUEST,
    payload: applicationId,
});

export const patchGroup = (id, obj, options) => ({
    type: PATCH_GROUP_REQUEST,
    payload: { id, obj, options },
});

export const createGroup = (obj, callback, options) => ({
    type: CREATE_GROUP_REQUEST,
    payload: { id: obj.id, obj, options },
    callback,
});

export const deleteGroup = (id, callback, options) => ({
    type: DELETE_GROUP_REQUEST,
    payload: { id, options },
    callback,
});

export const addSubgroup = (groupId, subGroupId, options) => ({
    type: ADD_SUBGROUP_REQUEST,
    payload: { groupId, subGroupId, options },
});

export const removeSubgroup = (groupId, subGroupId, options) => ({
    type: REMOVE_SUBGROUP_REQUEST,
    payload: { groupId, subGroupId, options },
});
