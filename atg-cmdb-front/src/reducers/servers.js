import * as Constants from '../constants';

export function serverListIsLoading(state = null, action) {
    switch (action.type) {
        case Constants.FETCH_SERVER_LIST_REQUEST:
            return true;
        case Constants.FETCH_SERVER_LIST_RESPONSE:
            return false;
        default:
            return state;
    }
}

export function serverList(state = {}, action) {
    switch (action.type) {
        case Constants.FETCH_SERVER_LIST_RESPONSE:
            if (action.error) return {};
            return action.payload;
        default:
            return state;
    }
}
