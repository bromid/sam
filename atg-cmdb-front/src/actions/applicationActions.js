import * as Constants from '../constants';

export const fetchApplicationList = () => ({
    type: Constants.FETCH_APPLICATION_LIST_REQUEST,
});

export const fetchApplication = (applicationId) => ({
    type: Constants.FETCH_APPLICATION_REQUEST,
    payload: applicationId,
});

export const patchApplication = (id, data, options) => ({
    type: Constants.PATCH_APPLICATION_REQUEST,
    payload: { id, data, options },
});
