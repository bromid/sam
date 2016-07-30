import * as Constants from '../constants';
import createFetchReducer from './helpers/createFetchReducer';

const [info, fromInfo] = createFetchReducer({
    requestKey: Constants.FETCH_INFO_REQUEST,
    receiveKey: Constants.FETCH_INFO_RESPONSE,
});

export { fromInfo };
export default info;
