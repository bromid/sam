import fetch from 'isomorphic-fetch';

const credentials = btoa('web-gui:secret');

const BASE_OPTIONS = {
    headers: {
        'Authorization': 'Basic ' + credentials,
        'Accept': 'application/json',
        'Content-Type': 'application/json'
    }
}

export const fetchGroups = () => fetch('/services/group', BASE_OPTIONS).then(parseJSON);

export const fetchApplications = () => fetch('/services/application', BASE_OPTIONS).then(parseJSON);

export const fetchServers = () => fetch('/services/server', BASE_OPTIONS).then(parseJSON);

function parseJSON(response) {
    return response.json();
}
