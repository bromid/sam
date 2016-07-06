const keyRequired = (key) => { throw new Error(`${key} must be specified!`); };

export default function createFetchReducers(options) {
    const {
        resourceName = keyRequired('resourceName'),
        requestKey = keyRequired('requestKey'),
        receiveKey = keyRequired('receiveKey'),
    } = options;
    function resourceIsLoading(state = null, action) {
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

    return {
        [resourceName]: resource,
        [`${resourceName}IsLoading`]: resourceIsLoading,
    };
}
