import * as Constants from '../constants';

export function applicationsIsLoading(state = null, action) {
    switch (action.type) {
        case Constants.FETCH_APPLICATION_LIST_REQUEST:
            return true;
        case Constants.FETCH_APPLICATION_LIST_RESPONSE:
            return false;
        default:
            return state;
    }
}

export function applications(state = {}, action) {
    switch (action.type) {
        case Constants.FETCH_APPLICATION_LIST_RESPONSE:
            if (action.error) return {};
            return action.payload;
        default:
            return state;
    }
}
