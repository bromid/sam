import * as Constants from '../constants';

export function applicationsIsLoading(state = false, action) {
    switch (action.type) {
        case Constants.FETCH_APPLICATION_REQUEST:
            return true;
        case Constants.FETCH_APPLICATION_RESPONSE:
            return false;
        default:
            return state;
    }
}

export function applications(state = {}, action) {
    switch (action.type) {
        case Constants.FETCH_APPLICATION_RESPONSE:
            if (action.error) return {};
            return action.payload;
        default:
            return state;
    }
}
