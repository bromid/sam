import { takeLatest } from 'redux-saga';
import { fork } from 'redux-saga/effects';
import * as API from '../api';
import createFetchSaga from './helpers/createFetchSaga';
import {
    FETCH_SEARCH_REQUEST,
    FETCH_SEARCH_RESPONSE,
} from '../constants';

const fetchSearch = createFetchSaga({
    apiCall: API.fetchSearch,
    responseKey: FETCH_SEARCH_RESPONSE,
});

export default function* searchSagas() {
    yield fork(takeLatest, FETCH_SEARCH_REQUEST, fetchSearch);
}
