import {
    FETCH_SERVER_LIST_REQUEST,
    FETCH_SERVER_REQUEST,
    PATCH_SERVER_REQUEST,
    CREATE_SERVER_REQUEST,
    DELETE_SERVER_REQUEST,
} from '../constants';

export const fetchServerList = (environment) => ({
    type: FETCH_SERVER_LIST_REQUEST,
    payload: { environment },
});

export const fetchServer = (hostname, environment) => ({
    type: FETCH_SERVER_REQUEST,
    payload: { hostname, environment },
});

export const patchServer = (hostname, environment, obj, options) => ({
    type: PATCH_SERVER_REQUEST,
    payload: { hostname, environment, obj, options },
});

export const createServer = (obj, options) => ({
    type: CREATE_SERVER_REQUEST,
    payload: { hostname: obj.hostname, environment: obj.environment, obj, options },
});

export const deleteServer = (hostname, environment, options) => ({
    type: DELETE_SERVER_REQUEST,
    payload: { hostname, environment, options },
});
