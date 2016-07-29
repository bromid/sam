import { combineReducers } from 'redux';
import * as Constants from '../constants';
import createFetchReducers from '../createFetchReducers';

const [current, fromCurrent] = createFetchReducers({
    resourceName: 'group',
    requestKey: Constants.FETCH_GROUP_REQUEST,
    receiveKey: Constants.FETCH_GROUP_RESPONSE,
    flatReducers: false,
});

const [list, fromList] = createFetchReducers({
    requestKey: Constants.FETCH_GROUP_LIST_REQUEST,
    receiveKey: Constants.FETCH_GROUP_LIST_RESPONSE,
    flatReducers: false,
});

const [patchResult, fromPatchResult] = createFetchReducers({
    requestKey: Constants.PATCH_GROUP_REQUEST,
    receiveKey: Constants.PATCH_GROUP_RESPONSE,
    flatReducers: false,
});

const [tags, fromTags] = createFetchReducers({
    requestKey: Constants.FETCH_GROUP_TAG_REQUEST,
    receiveKey: Constants.FETCH_GROUP_TAG_RESPONSE,
    flatReducers: false,
});

export const getCurrent = (state) => fromCurrent.getData(state.current);
export const getCurrentIsPending = (state) => fromCurrent.getIsPending(state.current);
export const getCurrentError = (state) => fromCurrent.getError(state.current);

export const getList = (state) => fromList.getData(state.list).items;
export const getListIsPending = (state) => fromList.getIsPending(state.list);
export const getListError = (state) => fromList.getError(state.list);

export const getPatchResult = (state) => fromPatchResult.getData(state.patchResult);
export const getPatchResultIsPending = (state) => fromPatchResult.getIsPending(state.patchResult);
export const getPatchResultError = (state) => fromPatchResult.getError(state.patchResult);

export const getTags = (state) => fromTags.getData(state.tags).items;
export const getTagsIsPending = (state) => fromTags.getIsPending(state.tags);
export const getTagsError = (state) => fromTags.getError(state.tags);


export default combineReducers({
    current,
    list,
    patchResult,
    tags,
});
