import { combineReducers } from 'redux';
import * as Constants from '../constants';
import createFetchReducer from './helpers/createFetchReducer';
import createCRUDReducers from './helpers/createCRUDReducers';

const { CRUDReducers, CRUDSelectors } = createCRUDReducers('APPLICATION');

const [deployments, fromDeployments] = createFetchReducer({
    requestKey: Constants.FETCH_APPLICATION_REQUEST,
    receiveKey: Constants.FETCH_APPLICATION_DEPLOYMENTS_RESPONSE,
});

export const fromApplication = {
    ...CRUDSelectors,
    getDeployments: (state) => fromDeployments.getData(state.deployments).items,
    getDeploymentsIsPending: (state) => fromDeployments.getIsPending(state.deployments),
    getDeploymentsError: (state) => fromDeployments.getError(state.deployments),
};

export default combineReducers({
    ...CRUDReducers,
    deployments,
});
