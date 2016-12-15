import identity from 'lodash/identity';
import noop from 'lodash/noop';
import { call, put, select } from 'redux-saga/effects';
import { fromAuth } from '../../reducers';

const keyRequired = (key) => { throw new Error(`${key} must be specified!`); };

export default function createFetchSaga(options) {
    const {
        apiCall = keyRequired('ApiCall'),
        responseKey = keyRequired('responseKey'),
        payloadTransform = identity,
        errorTransform = identity,
        paramSelector = (action) => action.payload,
    } = options;

    return function* fetchSaga(action) {
        try {
            const apiParams = paramSelector(action);
            const authenticatedUser = yield select(fromAuth.getAuthenticatedUser);
            const payload = yield call(apiCall, apiParams, authenticatedUser);
            yield put({
                type: responseKey,
                request: action.payload,
                callback: action.callback || noop,
                payload: payloadTransform(payload.data, payload.response),
            });
        } catch (error) {
            if (process.env.NODE_ENV !== 'production') {
                console.error(error.stack); // eslint-disable-line no-console
            }

            yield put({
                type: responseKey,
                error: true,
                request: action.payload,
                callback: action.callback || noop,
                payload: errorTransform(error, error.response),
            });
        }
    };
}
