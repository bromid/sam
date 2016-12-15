import * as Constants from '../constants';
import createFetchReducer from './helpers/createFetchReducer';

const [searchResults, fromSearchResults] = createFetchReducer({
    requestKey: Constants.FETCH_SEARCH_REQUEST,
    receiveKey: Constants.FETCH_SEARCH_RESPONSE,
});

export { fromSearchResults };
export default searchResults;
