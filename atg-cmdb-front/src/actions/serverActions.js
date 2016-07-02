import createFetchActions from '../createFetchActions';
import * as Constants from '../constants';
import * as API from '../api';

export const fetchServerList = createFetchActions({
    apiCall: API.fetchServerList,
    requestKey: Constants.FETCH_SERVER_LIST_REQUEST,
    receiveKey: Constants.FETCH_SERVER_LIST_RESPONSE,
});

export const fetchServer = createFetchActions({
    apiCall: API.fetchServer,
    requestKey: Constants.FETCH_SERVER_REQUEST,
    receiveKey: Constants.FETCH_SERVER_RESPONSE,
});
