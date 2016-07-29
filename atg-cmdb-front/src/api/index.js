import fetch from 'isomorphic-fetch';
import isObject from 'lodash/isObject';

const APPLICATION_JSON = {
    headers: {
        Accept: 'application/json',
        'Content-Type': 'application/json',
    },
};

const TEXT_HTML = {
    headers: {
        Accept: 'text/html',
    },
};

const verifySuccessful = (response) => {
    if (response.status >= 400) {
        const error = new Error(
            `Got status ${response.statusText} (${response.status}) for ${response.url}.`
        );
        error.response = response;
        throw error;
    }
    return response;
};

const addParams = (url, params) => {
    if (!isObject(params)) return url;

    const query = Object.keys(params)
        .filter((name) => (params[name] !== undefined))
        .map((name) => `${name}=${params[name]}`)
        .join('&');
    return `${url}?${query}`;
};

const fetchJson = (url, params) => (
    fetch(addParams(url, params), APPLICATION_JSON)
        .then((response) => verifySuccessful(response))
        .then((response) =>
            response.json().then((data) => ({
                data,
                response,
            }))
        )
);

const fetchHtml = (url, params) => (
    fetch(addParams(url, params), TEXT_HTML)
        .then((response) => verifySuccessful(response))
        .then((response) =>
            response.text().then((data) => ({
                data,
                response,
            }))
        )
);

const patchJson = (url, obj, { hash, params } = {}) => {
    const headers = (hash) ? {
        ...APPLICATION_JSON.headers,
        'If-Match': `"${hash}"`,
    } : APPLICATION_JSON.headers;

    const options = {
        headers,
        method: 'PATCH',
        body: JSON.stringify(obj),
    };
    return fetch(addParams(url, params), options)
        .then((response) => verifySuccessful(response))
        .then((response) =>
            response.json().then((data) => ({
                data,
                response,
            }))
        );
};

const createJson = (url, obj, { params } = {}) => {
    const options = {
        headers: {
            ...APPLICATION_JSON.headers,
            'If-None-Match': '*',
        },
        method: 'PUT',
        body: JSON.stringify(obj),
    };
    return fetch(addParams(url, params), options)
        .then((response) => verifySuccessful(response))
        .then((response) =>
            response.json().then((data) => ({
                data,
                response,
            }))
        );
};

export const fetchGroupList = (queryParams) =>
    fetchJson('/services/group', queryParams);

export const fetchGroup = (groupId) =>
    fetchJson(`/services/group/${groupId}`);

export const fetchGroupTags = () =>
    fetchJson('/services/group/tag');

export const patchGroup = (params, auth) =>
    patchJson(`/services/group/${params.id}`, params, auth);

export const createGroup = (group, options) =>
    createJson(`/services/group/${group.id}`, group, options);

export const fetchApplicationList = () =>
    fetchJson('/services/application');

export const fetchApplication = (applicationId) =>
    fetchJson(`/services/application/${applicationId}`);

export const fetchApplicationDeployments = (applicationId) =>
    fetchJson(`/services/application/${applicationId}/deployment`);

export const patchApplication = (params, auth) =>
    patchJson(`/services/application/${params.id}`, params, auth);

export const createApplication = (application, options) =>
    createJson(`/services/application/${application.id}`, application, options);

export const fetchServerList = (params) => {
    if (params.environment) return fetchJson(`/services/server/${params.environment}`);
    return fetchJson('/services/server');
};

export const fetchServer = (params) =>
    fetchJson(`/services/server/${params.environment}/${params.hostname}`);

export const patchServer = (params, auth) =>
    patchJson(`/services/server/${params.environment}/${params.hostname}`, params, auth);

export const createServer = (server, options) =>
    createJson(`/services/server/${server.environment}/${server.hostname}`, server, options);

export const fetchAssetList = () =>
    fetchJson('/services/asset');

export const fetchAsset = (assetId) =>
    fetchJson(`/services/asset/${assetId}`);

export const patchAsset = (params, auth) =>
    patchJson(`/services/asset/${params.id}`, params, auth);

export const createAsset = (asset, options) =>
    createJson(`/services/asset/${asset.id}`, asset, options);

export const fetchSearch = (searchQuery) =>
    fetchJson(`/services/search?q=${searchQuery}`);

export const fetchInfo = () =>
    fetchJson('/services/info');

export const fetchReleaseNotes = () =>
    fetchHtml('/services/info/release-notes');
