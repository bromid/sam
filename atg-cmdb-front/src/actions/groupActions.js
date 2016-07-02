import createFetchActions from '../createFetchActions';
import * as Constants from '../constants';
import * as API from '../api';

export const fetchGroupList = createFetchActions({
    apiCall: API.fetchGroupList,
    requestKey: Constants.FETCH_GROUP_LIST_REQUEST,
    receiveKey: Constants.FETCH_GROUP_LIST_RESPONSE,
});

export const fetchGroup = createFetchActions({
    apiCall: API.fetchGroup,
    requestKey: Constants.FETCH_GROUP_REQUEST,
    receiveKey: Constants.FETCH_GROUP_RESPONSE,
});
