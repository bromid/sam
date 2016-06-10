import _ from 'lodash';

const keyRequired = (key) => { throw new Error(`${key} must be specified!`); };

export default function createFetchActions(options) {
    const {
        apiCall = keyRequired('ApiCall'),
        requestKey = keyRequired('requestKey'),
        receiveKey = keyRequired('receiveKey'),
        payloadTransform = _.identity,
        errorTransform = _.identity,
        shouldFetch,
    } = options;

    const requestPayload = () => ({ type: requestKey });

    const receivePayload = payload => ({
        type: receiveKey,
        payload: payloadTransform(payload),
    });

    const receivePayloadError = error => ({
        type: receiveKey,
        error: true,
        payload: {
            status: errorTransform(error),
        },
    });

    return function fetchPayload() {
        return (dispatch, getState) => {
            if (shouldFetch && !shouldFetch(getState())) return null;

            dispatch(requestPayload());
            return apiCall()
                .then(response => dispatch(receivePayload(response)))
                .catch(error => dispatch(receivePayloadError(error)));
        };
    };
}
