import createFetchActions from "../createFetchActions";
import * as Constants from "../constants";
import * as API from "../api";

export const fetchGroups = createFetchActions({
    apiCall: API.fetchGroups,
    requestKey: Constants.FETCH_GROUP_REQUEST,
    receiveKey: Constants.FETCH_GROUP_RESPONSE,
});
