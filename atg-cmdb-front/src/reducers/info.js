import * as Constants from '../constants';

export function infoIsLoading(state = null, action) {
    switch (action.type) {
        case Constants.FETCH_INFO_REQUEST:
            return true;
        case Constants.FETCH_INFO_RESPONSE:
            return false;
        default:
            return state;
    }
}

export function info(state = {}, action) {
    switch (action.type) {
        case Constants.FETCH_INFO_RESPONSE:
            if (action.error) return {};
            return action.payload;
        default:
            return state;
    }
}
