import fetch from 'isomorphic-fetch';
import isObject from 'lodash/isObject';

const credentials = btoa('web-gui:secret');

const APPLICATION_JSON = {
    headers: {
        Authorization: `Basic ${credentials}`,
        Accept: 'application/json',
        'Content-Type': 'application/json',
    },
};

const TEXT_HTML = {
    headers: {
        Authorization: `Basic ${credentials}`,
        Accept: 'text/html',
    },
};

const verifySuccessful = (response) => {
    if (response.status >= 400) {
        throw new Error(
            `Got status ${response.statusText} (${response.status}) for ${response.url}.`
        );
    }
    return response;
};

const addParams = (url, params) => {
    if (!isObject(params)) return url;

    const query = Object.keys(params)
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

function patchJson(url, obj, { hash, params } = {}) {
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
}

export const fetchGroupList = (queryParams) => fetchJson('/services/group', queryParams);

export const fetchGroup = (groupId) => fetchJson(`/services/group/${groupId}`);

export const fetchGroupTags = () => fetchJson('/services/group/tag');

export const fetchApplicationList = () => fetchJson('/services/application');

export const fetchApplication = (applicationId) =>
    fetchJson(`/services/application/${applicationId}`);

export const fetchApplicationDeployments = (applicationId) =>
    fetchJson(`/services/application/${applicationId}/deployment`);

export const patchApplication = (applicationId, obj, options) =>
    patchJson(`/services/application/${applicationId}`, obj, options);

export const fetchServerList = () => fetchJson('/services/server');

export const fetchServer = (params) =>
    fetchJson(`/services/server/${params.environment}/${params.hostname}`);

export const fetchAssetList = () => fetchJson('/services/asset');

export const fetchAsset = (assetId) => fetchJson(`/services/asset/${assetId}`);

export const fetchSearch = (searchQuery) => fetchJson(`/services/search?q=${searchQuery}`);

export const fetchInfo = () => fetchJson('/services/info');

export const fetchReleaseNotes = () => fetchHtml('/services/info/release-notes');
