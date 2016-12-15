import * as Constants from '../constants';
import createFetchReducer from './helpers/createFetchReducer';

const [releaseNotes, fromReleaseNotes] = createFetchReducer({
    requestKey: Constants.FETCH_RELEASE_NOTES_REQUEST,
    receiveKey: Constants.FETCH_RELEASE_NOTES_RESPONSE,
});

export { fromReleaseNotes };
export default releaseNotes;
