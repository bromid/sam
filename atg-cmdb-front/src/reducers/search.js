import * as Constants from '../constants';
import createFetchReducers from '../createFetchReducers';

export default createFetchReducers({
    resourceName: 'searchResults',
    requestKey: Constants.FETCH_SEARCH_REQUEST,
    receiveKey: Constants.FETCH_SEARCH_RESPONSE,
});
