import { combineReducers } from 'redux';
import {
    FETCH_GROUP_REQUEST,
    FETCH_GROUP_TAG_REQUEST,
    FETCH_GROUP_TAG_RESPONSE,
    FETCH_GROUP_ID_REQUEST,
    FETCH_GROUP_ID_RESPONSE,
    FETCH_GROUP_DEPLOYMENTS_REQUEST,
    FETCH_GROUP_DEPLOYMENTS_RESPONSE,
} from '../constants';
import createFetchReducer from './helpers/createFetchReducer';
import createCRUDReducers from './helpers/createCRUDReducers';
import createFetchReducerTest from './helpers/createMultiRequestFetchReducer';

const { CRUDReducers, CRUDSelectors } = createCRUDReducers('GROUP');

const [currentDeployments, fromCurrentDeployments] = createFetchReducerTest({
    requestKey: FETCH_GROUP_DEPLOYMENTS_REQUEST,
    receiveKey: FETCH_GROUP_DEPLOYMENTS_RESPONSE,
    resetKey: FETCH_GROUP_REQUEST,
});

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
    getCurrentDeployments: (state) =>
        fromCurrentDeployments.getData(state.currentDeployments),
    getCurrentDeploymentsIsPending: (state) =>
        fromCurrentDeployments.getIsPending(state.currentDeployments),
    getCurrentDeploymentsError: (state) =>
        fromCurrentDeployments.getError(state.currentDeployments),
};

export default combineReducers({
    ...CRUDReducers,
    currentDeployments,
    tags,
    ids,
});
