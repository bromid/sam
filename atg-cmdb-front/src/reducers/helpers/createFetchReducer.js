import { combineReducers } from 'redux';
const keyRequired = (key) => { throw new Error(`${key} must be specified!`); };

export default function createFetchReducer(options) {
    const {
        requestKey = keyRequired('requestKey'),
        receiveKey = keyRequired('receiveKey'),
    } = options;

    function resourceIsPending(state = false, action) {
        switch (action.type) {
            case requestKey:
                return true;
            case receiveKey:
                return false;
            default:
                return state;
        }
    }

    function resource(state = {}, action) {
        switch (action.type) {
            case receiveKey:
                if (action.error) return {};
                return action.payload;
            default:
                return state;
        }
    }

    function error(state = {}, action) {
        switch (action.type) {
            case receiveKey:
                if (!action.error) return {};
                return {
                    ...action.payload,
                    error: true,
                    message: action.payload.message,
                };
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
