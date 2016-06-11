import appJSON from './applications.json';
import groupsJSON from './groups.json';
import serversJSON from './servers.json';

// This is a fake in-memory implementation of something
// that would be implemented by calling a REST server.

const delay = (ms) =>
    new Promise(resolve => setTimeout(resolve, ms));

export const fetchServers = () =>
    delay(300).then(() => serversJSON);

export const fetchGroups = () =>
    delay(300).then(() => groupsJSON);

export const fetchApplications = () =>
    delay(300).then(() => appJSON);
