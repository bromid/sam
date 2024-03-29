import { takeLatest } from 'redux-saga';
import { fork } from 'redux-saga/effects';
import * as API from '../api';
import createFetchSaga from './helpers/createFetchSaga';
import {
    FETCH_INFO_REQUEST,
    FETCH_INFO_RESPONSE,
    FETCH_RELEASE_NOTES_REQUEST,
    FETCH_RELEASE_NOTES_RESPONSE,
} from '../constants';

const fetchInfo = createFetchSaga({
    apiCall: API.fetchInfo,
    responseKey: FETCH_INFO_RESPONSE,
});

const fetchReleaseNotes = createFetchSaga({
    apiCall: API.fetchReleaseNotes,
    responseKey: FETCH_RELEASE_NOTES_RESPONSE,
});

export default function* infoSagas() {
    yield fork(takeLatest, FETCH_INFO_REQUEST, fetchInfo);
    yield fork(takeLatest, FETCH_RELEASE_NOTES_REQUEST, fetchReleaseNotes);
}
