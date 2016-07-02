import * as Constants from '../constants';

export function groupListIsLoading(state = null, action) {
    switch (action.type) {
        case Constants.FETCH_GROUP_LIST_REQUEST:
            return true;
        case Constants.FETCH_GROUP_LIST_RESPONSE:
            return false;
        default:
            return state;
    }
}

export function groupList(state = {}, action) {
    switch (action.type) {
        case Constants.FETCH_GROUP_LIST_RESPONSE:
            if (action.error) return {};
            return action.payload;
        default:
            return state;
    }
}
