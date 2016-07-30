import { combineReducers } from 'redux';
import * as Constants from '../constants';
import createFetchReducer from './helpers/createFetchReducer';
import createCRUDReducers from './helpers/createCRUDReducers';

const { CRUDReducers, CRUDSelectors } = createCRUDReducers('GROUP');

const [tags, fromTags] = createFetchReducer({
    requestKey: Constants.FETCH_GROUP_TAG_REQUEST,
    receiveKey: Constants.FETCH_GROUP_TAG_RESPONSE,
});

export const fromGroup = {
    ...CRUDSelectors,
    getTags: (state) => fromTags.getData(state.tags).items,
    getTagsIsPending: (state) => fromTags.getIsPending(state.tags),
    getTagsError: (state) => fromTags.getError(state.tags),
};

export default combineReducers({
    ...CRUDReducers,
    tags,
});
