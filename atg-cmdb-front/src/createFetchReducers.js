import { combineReducers } from 'redux';
const keyRequired = (key) => { throw new Error(`${key} must be specified!`); };

export default function createFetchReducers(options) {
    const {
        resourceName = options.flatReducers && keyRequired('resourceName'),
        requestKey = keyRequired('requestKey'),
        receiveKey = keyRequired('receiveKey'),
        flatReducers = true,
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
                return action.payload;
            default:
                return state;
        }
    }

    if (flatReducers) {
        return {
            [resourceName]: resource,
            [`${resourceName}Error`]: error,
            [`${resourceName}IsPending`]: resourceIsPending,
        };
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
