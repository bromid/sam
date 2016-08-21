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

const verifySuccessful = (response, params, options) => {
    if (response.status >= 400) {
        const error = new Error(
            `Got status ${response.statusText} (${response.status}) for ${response.url}.`
        );
        error.response = response;
        error.params = params;
        error.options = options;
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

const createOptions = (obj, method, headers, authenticated) => {
    const optionsHeaders = (authenticated) ? {
        ...headers,
        Authorization: `Basic ${btoa(`${authenticated.uid}:secret`)}`,
    } : headers;

    return {
        headers: optionsHeaders,
        method,
        body: JSON.stringify(obj),
    };
};

const fetchJson = (url, params, options) => (
    fetch(addParams(url, params), options)
        .then((response) => verifySuccessful(response, params, options))
        .then((response) =>
            response.json().then((data) => ({
                data,
                response,
            }))
        )
);

const getHtml = (url, params, options = {}) => {
    const { hash } = options;
    const headers = (hash) ? {
        ...TEXT_HTML.headers,
        'If-None-Match': `"${hash}"`,
    } : TEXT_HTML.headers;

    return fetch(addParams(url, params), { headers })
        .then((response) => verifySuccessful(response, params, { headers }))
        .then((response) =>
            response.text().then((data) => ({
                data,
                response,
            }))
        );
};

const getJson = (url, params, options = {}) => {
    const { hash } = options;
    const headers = (hash) ? {
        ...APPLICATION_JSON.headers,
        'If-None-Match': `"${hash}"`,
    } : APPLICATION_JSON.headers;

    return fetchJson(url, params, { headers });
};

const patchJson = (url, apiParams, authenticated) => {
    const { obj, options: { hash, params } = {} } = apiParams;
    const headers = (hash) ? {
        ...APPLICATION_JSON.headers,
        'If-Match': `"${hash}"`,
    } : APPLICATION_JSON.headers;

    const options = createOptions(obj, 'PATCH', headers, authenticated);
    return fetchJson(url, params, options);
};

const createJson = (url, apiParams, authenticated) => {
    const { obj, options: { params } = {} } = apiParams;
    const headers = {
        ...APPLICATION_JSON.headers,
        'If-None-Match': '*',
    };

    const options = createOptions(obj, 'PUT', headers, authenticated);
    return fetchJson(url, params, options);
};

export const fetchGroupList = (queryParams) =>
    getJson('/services/group', queryParams);

export const fetchGroup = (groupId) =>
    getJson(`/services/group/${groupId}`);

export const fetchGroupTags = () =>
    getJson('/services/group/tag');

export const patchGroup = (params, auth) =>
    patchJson(`/services/group/${params.id}`, params, auth);

export const createGroup = (params, auth) =>
    createJson(`/services/group/${params.id}`, params, auth);

export const fetchApplicationList = () =>
    getJson('/services/application');

export const fetchApplication = (applicationId) =>
    getJson(`/services/application/${applicationId}`);

export const fetchApplicationDeployments = (applicationId) =>
    getJson(`/services/application/${applicationId}/deployment`);

export const patchApplication = (params, auth) =>
    patchJson(`/services/application/${params.id}`, params, auth);

export const createApplication = (params, auth) =>
    createJson(`/services/application/${params.id}`, auth);

export const fetchServerList = (params) => {
    if (params.environment) return getJson(`/services/server/${params.environment}`);
    return getJson('/services/server');
};

export const fetchServer = (params) =>
    getJson(`/services/server/${params.environment}/${params.hostname}`);

export const patchServer = (params, auth) =>
    patchJson(`/services/server/${params.environment}/${params.hostname}`, params, auth);

export const createServer = (params, auth) =>
    createJson(`/services/server/${params.environment}/${params.hostname}`, params, auth);

export const fetchAssetList = () =>
    getJson('/services/asset');

export const fetchAsset = (assetId) =>
    getJson(`/services/asset/${assetId}`);

export const patchAsset = (params, auth) =>
    patchJson(`/services/asset/${params.id}`, params, auth);

export const createAsset = (params, auth) =>
    createJson(`/services/asset/${params.id}`, params, auth);

export const fetchSearch = (searchQuery) =>
    getJson(`/services/search?q=${searchQuery}`);

export const fetchInfo = () =>
    getJson('/services/info');

export const fetchReleaseNotes = () =>
    getHtml('/services/info/release-notes');
