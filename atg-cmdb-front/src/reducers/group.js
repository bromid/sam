import * as Constants from '../constants';
import createFetchReducers from '../createFetchReducers';

const group = createFetchReducers({
    resourceName: 'group',
    requestKey: Constants.FETCH_GROUP_REQUEST,
    receiveKey: Constants.FETCH_GROUP_RESPONSE,
});

const groupPatchResult = createFetchReducers({
    resourceName: 'groupPatchResult',
    requestKey: Constants.PATCH_GROUP_REQUEST,
    receiveKey: Constants.PATCH_GROUP_RESPONSE,
});

const groupTags = createFetchReducers({
    resourceName: 'groupTags',
    requestKey: Constants.FETCH_GROUP_TAG_REQUEST,
    receiveKey: Constants.FETCH_GROUP_TAG_RESPONSE,
});

const groupList = createFetchReducers({
    resourceName: 'groupList',
    requestKey: Constants.FETCH_GROUP_LIST_REQUEST,
    receiveKey: Constants.FETCH_GROUP_LIST_RESPONSE,
});

export default {
    ...group,
    ...groupPatchResult,
    ...groupTags,
    ...groupList,
};
