import * as Constants from '../constants';

export function serversIsLoading(state = null, action) {
    switch (action.type) {
        case Constants.FETCH_SERVER_REQUEST:
            return true;
        case Constants.FETCH_SERVER_RESPONSE:
            return false;
        default:
            return state;
    }
}

export function servers(state = {}, action) {
    switch (action.type) {
        case Constants.FETCH_SERVER_RESPONSE:
            if (action.error) return {};
            return action.payload;
        default:
            return state;
    }
}
