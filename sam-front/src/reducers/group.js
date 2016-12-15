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
import groupBy from 'lodash/groupBy';
import mapValues from 'lodash/mapValues';
import keys from 'lodash/keys';
import flatMap from 'lodash/flatMap';
import sortBy from 'lodash/sortBy';
import sortedUniq from 'lodash/sortedUniq';
import toLower from 'lodash/toLower';
import createFetchReducer from './helpers/createFetchReducer';
import createCRUDReducers from './helpers/createCRUDReducers';
import createFetchReducerTest from './helpers/createMultiRequestFetchReducer';

const environmentOrder = {
    qa: 1,
    stage: 2,
    internalprod: 3,
    prod: 4,
};

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

/*
    Selectors
 */
const byVersion = (deploymentsMap) => mapValues(deploymentsMap, (list) => groupBy(list, 'version'));

const byEnvironmentAndVersion = (deploymentsList) => {
    const byEnvironment = groupBy(deploymentsList, 'environment');
    return byVersion(byEnvironment);
};

const sortEnvironments = (environments) => sortBy(environments, [(value) => {
    const order = environmentOrder[toLower(value)];
    if (order) {
        return `z${order}`;
    }
    return value;
}]);

const getCurrentDeployments = (state) => fromCurrentDeployments.getData(state.currentDeployments);
const getCurrentDeploymentsByEnvironmentAndVersion = (state) => {
    const currentDeploymentsMap = getCurrentDeployments(state);
    return mapValues(currentDeploymentsMap, (deploymentsList) =>
        byEnvironmentAndVersion(deploymentsList.items)
    );
};
const getCurrentDeploymentsEnvironments = (state) => {
    const currentDeploymentsMap = getCurrentDeploymentsByEnvironmentAndVersion(state);
    const environments = flatMap(currentDeploymentsMap, (value) => keys(value));
    const sortedEnvironments = sortEnvironments(environments);
    return sortedUniq(sortedEnvironments);
};

export const fromGroup = {
    ...CRUDSelectors,
    getTags: (state) => fromTags.getData(state.tags).items,
    getTagsIsPending: (state) => fromTags.getIsPending(state.tags),
    getTagsError: (state) => fromTags.getError(state.tags),
    getIds: (state) => fromIds.getData(state.ids).items,
    getIdsIsPending: (state) => fromIds.getIsPending(state.ids),
    getIdsError: (state) => fromIds.getError(state.ids),
    getCurrentDeployments: (state) => getCurrentDeployments(state),
    getCurrentDeploymentsIsPending: (state) =>
        fromCurrentDeployments.getIsPending(state.currentDeployments),
    getCurrentDeploymentsError: (state) =>
        fromCurrentDeployments.getError(state.currentDeployments),
    getCurrentDeploymentsByEnvironmentAndVersion: (state) =>
        getCurrentDeploymentsByEnvironmentAndVersion(state),
    getCurrentDeploymentsEnvironments: (state) =>
        getCurrentDeploymentsEnvironments(state),
};

/*
    Reducers
 */
export default combineReducers({
    ...CRUDReducers,
    currentDeployments,
    tags,
    ids,
});
