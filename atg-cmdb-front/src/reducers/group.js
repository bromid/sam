import { combineReducers } from 'redux';
import {
    FETCH_GROUP_TAG_REQUEST,
    FETCH_GROUP_TAG_RESPONSE,
    FETCH_GROUP_ID_REQUEST,
    FETCH_GROUP_ID_RESPONSE,
} from '../constants';
import createFetchReducer from './helpers/createFetchReducer';
import createCRUDReducers from './helpers/createCRUDReducers';

const { CRUDReducers, CRUDSelectors } = createCRUDReducers('GROUP');

const [tags, fromTags] = createFetchReducer({
    requestKey: FETCH_GROUP_TAG_REQUEST,
    receiveKey: FETCH_GROUP_TAG_RESPONSE,
});

const [ids, fromIds] = createFetchReducer({
    requestKey: FETCH_GROUP_ID_REQUEST,
    receiveKey: FETCH_GROUP_ID_RESPONSE,
});

export const fromGroup = {
    ...CRUDSelectors,
    getTags: (state) => fromTags.getData(state.tags).items,
    getTagsIsPending: (state) => fromTags.getIsPending(state.tags),
    getTagsError: (state) => fromTags.getError(state.tags),
    getIds: (state) => fromIds.getData(state.ids).items,
    getIdsIsPending: (state) => fromIds.getIsPending(state.ids),
    getIdsError: (state) => fromIds.getError(state.ids),
};

export default combineReducers({
    ...CRUDReducers,
    tags,
    ids,
});
