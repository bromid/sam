import createFetchActions from '../createFetchActions';
import * as Constants from '../constants';
import * as API from '../api';

export const fetchApplications = createFetchActions({
    apiCall: API.fetchApplications,
    requestKey: Constants.FETCH_APPLICATION_REQUEST,
    receiveKey: Constants.FETCH_APPLICATION_RESPONSE,
});
