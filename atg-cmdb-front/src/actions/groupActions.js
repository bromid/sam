import {
    FETCH_GROUP_LIST_REQUEST,
    FETCH_GROUP_REQUEST,
    FETCH_GROUP_TAG_REQUEST,
    PATCH_GROUP_REQUEST,
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

export const patchGroup = (id, data, options) => ({
    type: PATCH_GROUP_REQUEST,
    payload: { id, data, options },
});
