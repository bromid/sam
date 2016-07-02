import * as Constants from '../constants';

export function serverIsLoading(state = null, action) {
    switch (action.type) {
        case Constants.FETCH_SERVER_REQUEST:
            return true;
        case Constants.FETCH_SERVER_RESPONSE:
            return false;
        default:
            return state;
    }
}

export function server(state = {}, action) {
    switch (action.type) {
        case Constants.FETCH_SERVER_RESPONSE:
            if (action.error) return {};
            return action.payload;
        default:
            return state;
    }
}
