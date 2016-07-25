import {
    FETCH_APPLICATION_LIST_REQUEST,
    FETCH_APPLICATION_REQUEST,
    PATCH_APPLICATION_REQUEST,
} from '../constants';

export const fetchApplicationList = () => ({
    type: FETCH_APPLICATION_LIST_REQUEST,
});

export const fetchApplication = (applicationId) => ({
    type: FETCH_APPLICATION_REQUEST,
    payload: applicationId,
});

export const patchApplication = (id, data, options) => ({
    type: PATCH_APPLICATION_REQUEST,
    payload: { id, data, options },
});
