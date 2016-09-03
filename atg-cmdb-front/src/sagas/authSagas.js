import { take, put } from 'redux-saga/effects';
import {
    LOGIN_REQUEST,
    LOGIN_RESPONSE,
    LOGOUT_REQUEST,
    LOGOUT_RESPONSE,
} from '../constants';

export default function* authSagas() {
    while (true) { // eslint-disable-line no-constant-condition
        yield take(LOGIN_REQUEST);
        yield put({ type: LOGIN_RESPONSE, user: { uid: 'web-gui-auth' } });
        yield take(LOGOUT_REQUEST);
        yield put({ type: LOGOUT_RESPONSE });
    }
}
