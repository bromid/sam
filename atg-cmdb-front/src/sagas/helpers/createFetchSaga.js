import identity from 'lodash/identity';
import { call, put } from 'redux-saga/effects';

const keyRequired = (key) => { throw new Error(`${key} must be specified!`); };

const getAPIParams = (paramSelector, params) => {
    const paramsAsArray = [].concat(params);
    if (!paramSelector) return paramsAsArray;

    return [].concat(paramSelector(...paramsAsArray));
};

export default function createFetchSaga(options) {
    const {
        apiCall = keyRequired('ApiCall'),
        responseKey = keyRequired('responseKey'),
        payloadTransform = identity,
        errorTransform = identity,
        paramSelector,
    } = options;

    return function* fetchSaga(...params) {
        try {
            const APIParams = getAPIParams(paramSelector, params);
            const payload = yield call(apiCall, ...APIParams);
            yield put({
                type: responseKey,
                payload: payloadTransform(payload.data, payload.response),
            });
        } catch (error) {
            yield put({
                type: responseKey,
                error: true,
                payload: errorTransform(error, error.response),
            });
        }
    };
}
