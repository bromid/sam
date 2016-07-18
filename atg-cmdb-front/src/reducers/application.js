import * as Constants from '../constants';
import createFetchReducers from '../createFetchReducers';

const application = createFetchReducers({
    resourceName: 'application',
    requestKey: Constants.FETCH_APPLICATION_REQUEST,
    receiveKey: Constants.FETCH_APPLICATION_RESPONSE,
});

const applicationPatchResult = createFetchReducers({
    resourceName: 'applicationPatchResult',
    requestKey: Constants.PATCH_APPLICATION_REQUEST,
    receiveKey: Constants.PATCH_APPLICATION_RESPONSE,
});

const applicationDeployments = createFetchReducers({
    resourceName: 'applicationDeployments',
    requestKey: Constants.FETCH_APPLICATION_DEPLOYMENTS_REQUEST,
    receiveKey: Constants.FETCH_APPLICATION_DEPLOYMENTS_RESPONSE,
});

const applicationList = createFetchReducers({
    resourceName: 'applicationList',
    requestKey: Constants.FETCH_APPLICATION_LIST_REQUEST,
    receiveKey: Constants.FETCH_APPLICATION_LIST_RESPONSE,
});

export default {
    ...application,
    ...applicationPatchResult,
    ...applicationDeployments,
    ...applicationList,
};
