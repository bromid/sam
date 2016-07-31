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

function fetchJson(url, params) {
    return fetch(addParams(url, params), APPLICATION_JSON)
        .then((response) => verifySuccessful(response))
        .then((response) =>
            response.json().then((data) => ({
                data,
                response,
            }))
        );
}

function fetchHtml(url, params) {
    return fetch(addParams(url, params), TEXT_HTML)
        .then((response) => verifySuccessful(response))
        .then((response) =>
            response.text().then((data) => ({
                data,
                response,
            }))
        );
}

function patchJson(url, obj, { hash, params } = {}, authenticated) {
    const headers = { ...APPLICATION_JSON.headers };
    if (hash) {
        headers['If-Match'] = `"${hash}"`;
    }

    if (authenticated) {
        const credential = btoa(`${authenticated.uid}:secret`);
        headers.Authorization = `Basic ${credential}`;
    }

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
}

export const fetchGroupList = (queryParams) =>
    fetchJson('/services/group', queryParams);

export const fetchGroup = (groupId) =>
    fetchJson(`/services/group/${groupId}`);

export const fetchGroupTags = () =>
    fetchJson('/services/group/tag');

export const patchGroup = (groupId, group, options, authenticated) =>
    patchJson(`/services/group/${groupId}`, group, options, authenticated);

export const fetchApplicationList = () =>
    fetchJson('/services/application');

export const fetchApplication = (applicationId) =>
    fetchJson(`/services/application/${applicationId}`);

export const fetchApplicationDeployments = (applicationId) =>
    fetchJson(`/services/application/${applicationId}/deployment`);

export const patchApplication = (applicationId, application, options, authenticated) =>
    patchJson(`/services/application/${applicationId}`, application, options, authenticated);

export const fetchServerList = (params) => {
    if (params.environment) return fetchJson(`/services/server/${params.environment}`);
    return fetchJson('/services/server');
};

export const fetchServer = (params) =>
    fetchJson(`/services/server/${params.environment}/${params.hostname}`);

export const patchServer = (params, server, options, authenticated) =>
    patchJson(`/services/server/${params.environment}/${params.hostname}`, server, options, authenticated);

export const fetchAssetList = () =>
    fetchJson('/services/asset');

export const fetchAsset = (assetId) =>
    fetchJson(`/services/asset/${assetId}`);

export const patchAsset = (assetId, asset, options, authenticated) =>
    patchJson(`/services/asset/${assetId}`, asset, options, authenticated);

export const fetchSearch = (searchQuery) =>
    fetchJson(`/services/search?q=${searchQuery}`);

export const fetchInfo = () =>
    fetchJson('/services/info');

export const fetchReleaseNotes = () =>
    fetchHtml('/services/info/release-notes');
