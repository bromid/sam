import fetch from 'isomorphic-fetch';

const credentials = btoa('web-gui:secret');

const BASE_OPTIONS = {
    headers: {
        Authorization: `Basic ${credentials}`,
        Accept: 'application/json',
        'Content-Type': 'application/json',
    },
};

const request = (url) => fetch(url, BASE_OPTIONS)
    .then((response) => response.json());

export const fetchGroupList = () => request('/services/group');

export const fetchGroup = (params) => request(`/services/group/${params}`);

export const fetchApplicationList = () => request('/services/application');

export const fetchApplication = (params) => request(`/services/application/${params}`);

export const fetchServerList = () => request('/services/server');

export const fetchServer = ({ environment, hostname }) =>
    request(`/services/server/${environment}/${hostname}`);

export const fetchSearch = (params) => request(`/services/search?q=${params}`);
