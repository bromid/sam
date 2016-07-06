import fetch from 'isomorphic-fetch';

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

const fetchJson = (url) => fetch(url, APPLICATION_JSON)
    .then((response) => verifySuccessful(response))
    .then((response) => response.json());

const fetchHtml = (url) => fetch(url, TEXT_HTML)
    .then((response) => verifySuccessful(response))
    .then((response) => response.text());

export const fetchGroupList = () => fetchJson('/services/group');

export const fetchGroup = (params) => fetchJson(`/services/group/${params}`);

export const fetchApplicationList = () => fetchJson('/services/application');

export const fetchApplication = (params) => fetchJson(`/services/application/${params}`);

export const fetchServerList = () => fetchJson('/services/server');

export const fetchServer = (params) =>
    fetchJson(`/services/server/${params.environment}/${params.hostname}`);

export const fetchAssetList = () => fetchJson('/services/asset');

export const fetchAsset = (params) => fetchJson(`/services/asset/${params}`);

export const fetchSearch = (params) => fetchJson(`/services/search?q=${params}`);

export const fetchInfo = () => fetchJson('/services/info');

export const fetchReleaseNotes = () => fetchHtml('/services/info/release-notes');
