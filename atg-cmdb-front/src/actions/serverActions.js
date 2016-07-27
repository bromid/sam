import {
    FETCH_SERVER_LIST_REQUEST,
    FETCH_SERVER_REQUEST,
    PATCH_SERVER_REQUEST,
} from '../constants';

export const fetchServerList = (environment) => ({
    type: FETCH_SERVER_LIST_REQUEST,
    payload: { environment },
});

export const fetchServer = (hostname, environment) => ({
    type: FETCH_SERVER_REQUEST,
    payload: { hostname, environment },
});

export const patchServer = (hostname, environment, server, options) => ({
    type: PATCH_SERVER_REQUEST,
    payload: [{ hostname, environment }, server, options],
});
