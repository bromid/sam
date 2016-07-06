import * as Constants from '../constants';
import createFetchReducers from '../createFetchReducers';

export default createFetchReducers({
    resourceName: 'releaseNotes',
    requestKey: Constants.FETCH_RELEASE_NOTES_REQUEST,
    receiveKey: Constants.FETCH_RELEASE_NOTES_RESPONSE,
});
