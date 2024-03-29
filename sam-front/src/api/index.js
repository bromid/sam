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

const createOptions = (obj, method, headers, authenticatedUser) => {
    const optionsHeaders = (authenticatedUser) ? {
        ...headers,
        Authorization: `Bearer ${authenticatedUser.idToken}`,
    } : headers;

    return {
        headers: optionsHeaders,
        method,
        body: JSON.stringify(obj),
    };
};

const fetchText = (url, params, options) => (
    fetch(addParams(url, params), options)
        .then((response) => verifySuccessful(response, params, options))
        .then((response) =>
            response.text().then((data) => ({
                data,
                response,
            }))
        )
);

const fetchJson = (url, params, options) => (
    fetchText(url, params, options)
        .then(({ data, response }) =>
            ({
                data: (data) ? JSON.parse(data) : null,
                response,
            })
        )
);

const getHtml = (url, params, options = {}) => {
    const { hash } = options;
    const headers = (hash) ? {
        ...TEXT_HTML.headers,
        'If-None-Match': `"${hash}"`,
    } : TEXT_HTML.headers;

    return fetchText(url, params, { headers });
};

const getJson = (url, params, options = {}) => {
    const { hash } = options;
    const headers = (hash) ? {
        ...APPLICATION_JSON.headers,
        'If-None-Match': `"${hash}"`,
    } : APPLICATION_JSON.headers;

    return fetchJson(url, params, { headers });
};

const patchJson = (url, apiParams, authenticatedUser) => {
    const { obj, options: { hash, params } = {} } = apiParams;
    const headers = (hash) ? {
        ...APPLICATION_JSON.headers,
        'If-Match': `"${hash}"`,
    } : APPLICATION_JSON.headers;

    const options = createOptions(obj, 'PATCH', headers, authenticatedUser);
    return fetchJson(url, params, options);
};

const updateJson = (url, apiParams, authenticatedUser) => {
    const { obj, options: { params } = {} } = apiParams;
    const options = createOptions(obj, 'PUT', APPLICATION_JSON.headers, authenticatedUser);
    return fetchJson(url, params, options);
};

const deleteJson = (url, apiParams, authenticatedUser) => {
    const { obj, options: { params } = {} } = apiParams;
    const options = createOptions(obj, 'DELETE', APPLICATION_JSON.headers, authenticatedUser);
    return fetchJson(url, params, options);
};

const createJson = (url, apiParams, authenticatedUser) => {
    const { obj, options: { params } = {} } = apiParams;
    const headers = {
        ...APPLICATION_JSON.headers,
        'If-None-Match': '*',
    };

    const options = createOptions(obj, 'PUT', headers, authenticatedUser);
    return fetchJson(url, params, options);
};

const postJson = (url, apiParams, authenticatedUser) => {
    const { obj, options: { params } = {} } = apiParams;
    const options = createOptions(obj, 'POST', APPLICATION_JSON.headers, authenticatedUser);
    return fetchJson(url, params, options);
};

export const fetchGroupList = (queryParams) =>
    getJson('/services/group', queryParams);

export const fetchGroup = (groupId) =>
    getJson(`/services/group/${groupId}`);

export const fetchGroupTags = () =>
    getJson('/services/group/tag');

export const fetchGroupIds = () =>
    getJson('/services/group/id');

export const patchGroup = (params, auth) =>
    patchJson(`/services/group/${params.id}`, params, auth);

export const createGroup = (params, auth) =>
    createJson(`/services/group/${params.id}`, params, auth);

export const deleteGroup = (params, auth) =>
    deleteJson(`/services/group/${params.id}`, params, auth);

export const addSubgroup = (params, auth) =>
    updateJson(`/services/group/${params.groupId}/group/${params.subGroupId}`, params, auth);

export const removeSubgroup = (params, auth) =>
    deleteJson(`/services/group/${params.groupId}/group/${params.subGroupId}`, params, auth);

export const fetchApplicationList = () =>
    getJson('/services/application');

export const fetchApplication = (applicationId) =>
    getJson(`/services/application/${applicationId}`);

export const fetchApplicationDeployments = (applicationId) =>
    getJson(`/services/application/${applicationId}/deployment`);

export const patchApplication = (params, auth) =>
    patchJson(`/services/application/${params.id}`, params, auth);

export const createApplication = (params, auth) =>
    createJson(`/services/application/${params.id}`, params, auth);

export const deleteApplication = (params, auth) =>
    deleteJson(`/services/application/${params.id}`, params, auth);

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

export const deleteServer = (params, auth) =>
    deleteJson(`/services/server/${params.environment}/${params.hostname}`, params, auth);

export const fetchAssetList = () =>
    getJson('/services/asset');

export const fetchAsset = (assetId) =>
    getJson(`/services/asset/${assetId}`);

export const patchAsset = (params, auth) =>
    patchJson(`/services/asset/${params.id}`, params, auth);

export const createAsset = (params, auth) =>
    createJson(`/services/asset/${params.id}`, params, auth);

export const deleteAsset = (params, auth) =>
    deleteJson(`/services/asset/${params.id}`, params, auth);

export const fetchSearch = (searchQuery) =>
    getJson(`/services/search?q=${searchQuery}`);

export const fetchInfo = () =>
    getJson('/services/info');

export const fetchReleaseNotes = () =>
    getHtml('/services/info/release-notes');

export const verifyOAuthCode = (params) =>
    postJson('/services/oauth2/token', params);
