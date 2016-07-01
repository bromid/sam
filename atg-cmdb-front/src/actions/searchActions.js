import createFetchActions from '../createFetchActions';
import * as Constants from '../constants';
import * as API from '../api';

export const fetchSearch = createFetchActions({
    apiCall: API.fetchSearch,
    requestKey: Constants.FETCH_SEARCH_REQUEST,
    receiveKey: Constants.FETCH_SEARCH_RESPONSE
});
