import * as Constants from '../constants';
import * as API from '../api';

function requestServer() {
    return {
        type: Constants.FETCH_SERVER_REQUEST
    };
}

function receiveServer(servers) {
    return {
        type: Constants.FETCH_SERVER_RESPONSE,
        payload: servers
    }
}

export function fetchServers() {
    return (dispatch) => {
        dispatch(requestServer());
        API.fetchServers().then(servers => dispatch(receiveServer(servers)));
    }
}