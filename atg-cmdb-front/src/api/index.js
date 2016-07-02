import fetch from 'isomorphic-fetch';

const credentials = btoa('web-gui:secret');

const BASE_OPTIONS = {
    headers: {
        'Authorization': `Basic ${credentials}`,
        'Accept': 'application/json',
        'Content-Type': 'application/json'
    }
}

export const fetchGroupList = () => fetch('/services/group', BASE_OPTIONS).then(parseJSON);
export const fetchGroup = (params) => fetch(`/services/group/${params}`, BASE_OPTIONS).then(parseJSON);

export const fetchApplicationList = () => fetch('/services/application', BASE_OPTIONS).then(parseJSON);
export const fetchApplication = (params) => fetch(`/services/application/${params}`, BASE_OPTIONS).then(parseJSON);

export const fetchServerList = () => fetch('/services/server', BASE_OPTIONS).then(parseJSON);
export const fetchServer = (params) => fetch(`/services/server/${params.environment}/${params.hostname}`, BASE_OPTIONS).then(parseJSON);

export const fetchSearch = (params) => fetch(`/services/search?q=${params}`, BASE_OPTIONS).then(parseJSON);

function parseJSON(response) {
    return response.json();
}
