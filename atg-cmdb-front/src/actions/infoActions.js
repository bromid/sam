import createFetchActions from '../createFetchActions';
import * as Constants from '../constants';
import * as API from '../api';

export const fetchInfo = createFetchActions({
    apiCall: API.fetchInfo,
    requestKey: Constants.FETCH_INFO_REQUEST,
    receiveKey: Constants.FETCH_INFO_RESPONSE,
});

export const fetchReleaseNotes = createFetchActions({
    apiCall: API.fetchReleaseNotes,
    requestKey: Constants.FETCH_RELEASE_NOTES_REQUEST,
    receiveKey: Constants.FETCH_RELEASE_NOTES_RESPONSE,
});
