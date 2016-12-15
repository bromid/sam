import {
    FETCH_INFO_REQUEST,
    FETCH_RELEASE_NOTES_REQUEST,
} from '../constants';

export const fetchInfo = () => ({
    type: FETCH_INFO_REQUEST,
});

export const fetchReleaseNotes = () => ({
    type: FETCH_RELEASE_NOTES_REQUEST,
});
