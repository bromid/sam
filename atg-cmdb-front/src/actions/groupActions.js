import {
    FETCH_GROUP_LIST_REQUEST,
    FETCH_GROUP_REQUEST,
    FETCH_GROUP_TAG_REQUEST,
    FETCH_GROUP_ID_REQUEST,
    PATCH_GROUP_REQUEST,
    CREATE_GROUP_REQUEST,
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

export const patchGroup = (id, obj, options) => ({
    type: PATCH_GROUP_REQUEST,
    payload: { id, obj, options },
});

export const createGroup = (obj, options) => ({
    type: CREATE_GROUP_REQUEST,
    payload: { id: obj.id, obj, options },
});
