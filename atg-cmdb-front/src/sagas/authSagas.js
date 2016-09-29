import { fork, take, put, call, race } from 'redux-saga/effects';
import { timeout } from './helpers';
import jwtDecode from 'jwt-decode';
import {
    SIGNIN_REQUEST,
    SIGNIN_RESPONSE,
    SIGNOUT_REQUEST,
    SIGNOUT_RESPONSE,
    VERIFY_OAUTH_CODE,
    SIGNIN_IFRAME_RESPONSE,
    SIGNIN_WINDOW_CLOSED,
} from '../constants';
import * as authActions from '../actions/authActions';
import * as API from '../api';
import * as LOG from '../helpers/log';

function* verifyOAuthCode(codeRequest, loginRequest) {
    LOG.debug('Verify oauth code', codeRequest, loginRequest);
    try {
        if (codeRequest.cancelled) {
            return false;
        }

        const { code, state } = codeRequest.response;
        const requestState = loginRequest.payload.state;
        if (state !== requestState) {
            throw new Error(
                `Invalid state in authorization response. Expected ${requestState} got ${state}`
            );
        }

        const apiCall = call(API.verifyOAuthCode, { obj: { code, state } });
        const verifyCode = yield timeout(apiCall, 1000);
        if (verifyCode.cancelled) {
            return false;
        }

        const { id_token } = verifyCode.response.data;
        return {
            idToken: id_token,
            jwt: jwtDecode(id_token),
        };
    } catch (err) {
        LOG.error(err);
        return false;
    }
}

function* takeOauthCode() {
    const action = yield take(VERIFY_OAUTH_CODE);
    return action.payload;
}

function* signinResult({ jwt, idToken }) {
    yield put({ type: SIGNIN_RESPONSE, user: { uid: jwt.sub, idToken } });
    return true;
}

function* iframeSignin() {
    const iframeRequest = yield put(authActions.signinIframe());

    const codeTask = yield fork(takeOauthCode);
    yield timeout(SIGNIN_IFRAME_RESPONSE, 2000);

    const codeRequest = yield timeout(call(() => codeTask.done), 200);
    return yield* verifyOAuthCode(codeRequest, iframeRequest);
}

function* windowSignin() {
    const windowRequest = yield put(authActions.signinWindow());

    const codeRequest = yield race({
        response: call(takeOauthCode),
        cancelled: take(SIGNIN_WINDOW_CLOSED),
    });

    yield put(authActions.closeSigninWindow());
    return yield* verifyOAuthCode(codeRequest, windowRequest);
}

function* signin() {
    const { payload: { silent } } = yield take(SIGNIN_REQUEST);

    const iframeResult = yield* iframeSignin();
    if (iframeResult) {
        return yield* signinResult(iframeResult);
    }

    if (!silent) {
        const windowResult = yield* windowSignin();
        if (windowResult) {
            return yield* signinResult(windowResult);
        }
    }

    yield put({ type: SIGNIN_RESPONSE, user: null });
    return false;
}

function* signout() {
    yield take(SIGNOUT_REQUEST);
    yield put({ type: SIGNOUT_RESPONSE });
}

export default function* authSagas() {
    while (true) { // eslint-disable-line no-constant-condition
        const success = yield* signin();
        if (success) {
            yield* signout();
        }
    }
}
