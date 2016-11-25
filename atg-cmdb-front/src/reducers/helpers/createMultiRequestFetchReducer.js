import { combineReducers } from 'redux';
const keyRequired = (key) => { throw new Error(`${key} must be specified!`); };

/*
    Creates three reducers for data, isPending and errors.
    The reducer is keeping track of multiple requests. Each request is appended to
    the state by it's request id.
 */
export default function createFetchReducer(options) {
    const {
        requestKey = keyRequired('requestKey'),
        receiveKey = keyRequired('receiveKey'),
        resetKey,
    } = options;

    function resourceIsPending(state = {}, action) {
        switch (action.type) {
            case requestKey:
                return {
                    ...state,
                    [action.payload]: true,
                };
            case receiveKey:
                return {
                    ...state,
                    [action.request]: false,
                };
            case resetKey:
                return {};
            default:
                return state;
        }
    }

    function resource(state = {}, action) {
        switch (action.type) {
            case receiveKey:
                if (action.error) {
                    return {
                        ...state,
                        [action.request]: null,
                    };
                }
                return {
                    ...state,
                    [action.request]: action.payload,
                };
            case resetKey:
                return {};
            default:
                return state;
        }
    }

    function error(state = {}, action) {
        switch (action.type) {
            case receiveKey:
                if (!action.error) {
                    return {
                        ...state,
                        [action.request]: null,
                    };
                }
                return {
                    ...state,
                    [action.request]: {
                        ...action.payload,
                        error: true,
                        message: action.payload.message,
                    },
                };
            case resetKey:
                return {};
            default:
                return state;
        }
    }

    return [
        combineReducers({
            data: resource,
            isPending: resourceIsPending,
            error,
        }), {
            getData: (state) => state.data,
            getIsPending: (state) => state.isPending,
            getError: (state) => state.error,
        },
    ];
}
