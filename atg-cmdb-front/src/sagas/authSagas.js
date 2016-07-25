import { take, fork, put } from 'redux-saga/effects';
import {
    LOGIN_REQUEST,
    LOGIN_RESPONSE,
    LOGOUT_REQUEST,
    LOGOUT_RESPONSE,
} from '../constants';

/** Watch-sagas start **/

export function* watchLoginLogoutRequest() {
    while (true) { // eslint-disable-line no-constant-condition
        yield take(LOGIN_REQUEST);
        yield put({ type: LOGIN_RESPONSE, user: { uid: 'web-gui-auth' } });
        yield take(LOGOUT_REQUEST);
        yield put({ type: LOGOUT_RESPONSE });
    }
}

export default function* authSagas() {
    yield fork(watchLoginLogoutRequest);
}
