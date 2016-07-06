import * as Constants from '../constants';

export function assetListIsLoading(state = null, action) {
    switch (action.type) {
        case Constants.FETCH_ASSET_LIST_REQUEST:
            return true;
        case Constants.FETCH_ASSET_LIST_RESPONSE:
            return false;
        default:
            return state;
    }
}

export function assetList(state = {}, action) {
    switch (action.type) {
        case Constants.FETCH_ASSET_LIST_RESPONSE:
            if (action.error) return {};
            return action.payload;
        default:
            return state;
    }
}
