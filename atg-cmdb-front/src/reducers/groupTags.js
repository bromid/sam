import * as Constants from '../constants';
import createFetchReducers from '../createFetchReducers';

export default createFetchReducers({
    resourceName: 'groupTags',
    requestKey: Constants.FETCH_GROUP_TAG_REQUEST,
    receiveKey: Constants.FETCH_GROUP_TAG_RESPONSE,
});

