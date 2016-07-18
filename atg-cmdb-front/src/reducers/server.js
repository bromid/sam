import * as Constants from '../constants';
import createFetchReducers from '../createFetchReducers';

const server = createFetchReducers({
    resourceName: 'server',
    requestKey: Constants.FETCH_SERVER_REQUEST,
    receiveKey: Constants.FETCH_SERVER_RESPONSE,
});

const serverList = createFetchReducers({
    resourceName: 'serverList',
    requestKey: Constants.FETCH_SERVER_LIST_REQUEST,
    receiveKey: Constants.FETCH_SERVER_LIST_RESPONSE,
});

export default {
    ...server,
    ...serverList,
};
