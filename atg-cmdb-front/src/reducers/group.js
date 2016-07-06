import * as Constants from '../constants';

export function groupIsLoading(state = null, action) {
    switch (action.type) {
        case Constants.FETCH_GROUP_REQUEST:
            return true;
        case Constants.FETCH_GROUP_RESPONSE:
            return false;
        default:
            return state;
    }
}

export function group(state = {}, action) {
    switch (action.type) {
        case Constants.FETCH_GROUP_RESPONSE:
            if (action.error) return {};
            return action.payload;
        default:
            return state;
    }
}
