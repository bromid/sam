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

export const patchServer = createFetchActions({
    apiCall: API.patchServer,
    requestKey: Constants.PATCH_SERVER_REQUEST,
    receiveKey: Constants.PATCH_SERVER_RESPONSE,
    payloadTransform: (data, response) => ({
        ...data,
        etag: response.headers.get('etag'),
    }),
});
