import * as Constants from '../constants';

export function assetIsLoading(state = null, action) {
    switch (action.type) {
        case Constants.FETCH_ASSET_REQUEST:
            return true;
        case Constants.FETCH_ASSET_RESPONSE:
            return false;
        default:
            return state;
    }
}

export function asset(state = {}, action) {
    switch (action.type) {
        case Constants.FETCH_ASSET_RESPONSE:
            if (action.error) return {};
            return action.payload;
        default:
            return state;
    }
}
