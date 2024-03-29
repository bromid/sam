import {
    FETCH_APPLICATION_LIST_REQUEST,
    FETCH_APPLICATION_REQUEST,
    PATCH_APPLICATION_REQUEST,
    CREATE_APPLICATION_REQUEST,
    DELETE_APPLICATION_REQUEST,
} from '../constants';

export const fetchApplicationList = () => ({
    type: FETCH_APPLICATION_LIST_REQUEST,
});

export const fetchApplication = (applicationId) => ({
    type: FETCH_APPLICATION_REQUEST,
    payload: { id: applicationId },
});

export const patchApplication = (id, obj, options) => ({
    type: PATCH_APPLICATION_REQUEST,
    payload: { id, obj, options },
});

export const createApplication = (obj, options) => ({
    type: CREATE_APPLICATION_REQUEST,
    payload: { id: obj.id, obj, options },
});

export const deleteApplication = (id, options) => ({
    type: DELETE_APPLICATION_REQUEST,
    payload: { id, options },
});
