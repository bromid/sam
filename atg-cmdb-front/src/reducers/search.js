import * as Constants from "../constants";

export function searchIsLoading(state = false, action) {
    switch (action.type) {
        case Constants.FETCH_SEARCH_REQUEST:
            return true;
        case Constants.FETCH_SEARCH_RESPONSE:
            return false;
        default:
            return state;
    }
}

export function searchResults(state = {}, action) {
    switch (action.type) {
        case Constants.FETCH_SEARCH_RESPONSE:
            if (action.error) return {};
            return action.payload;
        default:
            return state;
    }
}