import identity from 'lodash/identity';
import { call, put, select } from 'redux-saga/effects';
import { toArray } from '../../helpers';
import { getAuthenticated } from '../../reducers';

const keyRequired = (key) => { throw new Error(`${key} must be specified!`); };

export default function createFetchSaga(options) {
    const {
        apiCall = keyRequired('ApiCall'),
        responseKey = keyRequired('responseKey'),
        payloadTransform = identity,
        errorTransform = identity,
        paramSelector = (action) => action.payload,
    } = options;

    return function* fetchSaga(...params) {
        try {
            const APIParams = toArray(paramSelector(...params));
            const authenticated = yield select(getAuthenticated);
            const payload = yield call(apiCall, ...APIParams, authenticated);
            yield put({
                type: responseKey,
                payload: payloadTransform(payload.data, payload.response),
            });
        } catch (error) {
            if (process.env.NODE_ENV !== 'production') {
                console.error(error.stack); // eslint-disable-line no-console
            }

            yield put({
                type: responseKey,
                error: true,
                payload: errorTransform(error, error.response),
            });
        }
    };
}
