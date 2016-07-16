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

const fetchJson = (url, params) => fetch(addParams(url, params), APPLICATION_JSON)
    .then((response) => verifySuccessful(response))
    .then((response) => response.json());

const fetchHtml = (url) => fetch(url, TEXT_HTML)
    .then((response) => verifySuccessful(response))
    .then((response) => response.text());

export const fetchGroupList = (queryParams) => fetchJson('/services/group', queryParams);

export const fetchGroup = (groupId) => fetchJson(`/services/group/${groupId}`);

export const fetchGroupTags = () => fetchJson('/services/group/tag');

export const fetchApplicationList = () => fetchJson('/services/application');

export const fetchApplication = (applicationId) =>
    fetchJson(`/services/application/${applicationId}`);

export const fetchApplicationDeployments = (applicationId) =>
    fetchJson(`/services/application/${applicationId}/deployment`);

export const fetchServerList = () => fetchJson('/services/server');

export const fetchServer = (params) =>
    fetchJson(`/services/server/${params.environment}/${params.hostname}`);

export const fetchAssetList = () => fetchJson('/services/asset');

export const fetchAsset = (assetId) => fetchJson(`/services/asset/${assetId}`);

export const fetchSearch = (searchQuery) => fetchJson(`/services/search?q=${searchQuery}`);

export const fetchInfo = () => fetchJson('/services/info');

export const fetchReleaseNotes = () => fetchHtml('/services/info/release-notes');
