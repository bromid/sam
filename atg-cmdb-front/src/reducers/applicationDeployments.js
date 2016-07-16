import * as Constants from '../constants';
import createFetchReducers from '../createFetchReducers';

export default createFetchReducers({
    resourceName: 'applicationDeployments',
    requestKey: Constants.FETCH_APPLICATION_DEPLOYMENTS_REQUEST,
    receiveKey: Constants.FETCH_APPLICATION_DEPLOYMENTS_RESPONSE,
});
