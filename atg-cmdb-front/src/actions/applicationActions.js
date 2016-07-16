import createFetchActions from '../createFetchActions';
import * as Constants from '../constants';
import * as API from '../api';

export const fetchApplicationList = createFetchActions({
    apiCall: API.fetchApplicationList,
    requestKey: Constants.FETCH_APPLICATION_LIST_REQUEST,
    receiveKey: Constants.FETCH_APPLICATION_LIST_RESPONSE,
});

export const fetchApplication = createFetchActions({
    apiCall: API.fetchApplication,
    requestKey: Constants.FETCH_APPLICATION_REQUEST,
    receiveKey: Constants.FETCH_APPLICATION_RESPONSE,
});

export const fetchApplicationDeployments = createFetchActions({
    apiCall: API.fetchApplicationDeployments,
    requestKey: Constants.FETCH_APPLICATION_DEPLOYMENTS_REQUEST,
    receiveKey: Constants.FETCH_APPLICATION_DEPLOYMENTS_RESPONSE,
});
