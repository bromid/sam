import { FETCH_SEARCH_REQUEST } from '../constants';

export const fetchSearch = (searchString) => ({
    type: FETCH_SEARCH_REQUEST,
    payload: searchString,
});
