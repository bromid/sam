import serversJSON from './servers.json';
import fetch from 'isomorphic-fetch';

// This is a fake in-memory implementation of something
// that would be implemented by calling a REST server.

const delay = (ms) =>
    new Promise(resolve => setTimeout(resolve, ms));

export const fetchServers = () =>
    delay(1000).then(() => serversJSON);

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

function parseJSON(response) {
    return response.json();
}
