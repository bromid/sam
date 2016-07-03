import * as Constants from '../constants';

export function releaseNotesIsLoading(state = null, action) {
    switch (action.type) {
        case Constants.FETCH_RELEASE_NOTES_REQUEST:
            return true;
        case Constants.FETCH_RELEASE_NOTES_RESPONSE:
            return false;
        default:
            return state;
    }
}

export function releaseNotes(state = {}, action) {
    switch (action.type) {
        case Constants.FETCH_RELEASE_NOTES_RESPONSE:
            if (action.error) return {};
            return action.payload;
        default:
            return state;
    }
}
