import uuid from 'node-uuid';
import {
    SIGNIN_REQUEST,
    SIGNOUT_REQUEST,
    VERIFY_OAUTH_CODE,
    SIGNIN_WINDOW_OPEN,
    SIGNIN_WINDOW_CLOSE,
    SIGNIN_WINDOW_CLOSED,
    SIGNIN_IFRAME_REQUEST,
    SIGNIN_IFRAME_RESPONSE,
} from '../constants';

const oauthRequest = (state) => {
    const settings = window.samSettings.oauth;
    return [
        `${settings.url}?client_id=${settings.clientId}`,
        `scope=${settings.scopes}`,
        `state=${state}`,
        `redirect_uri=${encodeURIComponent(settings.origin + '/oauth')}`,
        'response_type=code',
    ].join('&');
};

export const signin = (silent = false) => ({
    type: SIGNIN_REQUEST,
    payload: { silent },
});

export const signout = () => ({
    type: SIGNOUT_REQUEST,
});

export const verifyOAuthCode = (code, state) => ({
    type: VERIFY_OAUTH_CODE,
    payload: { code, state },
});

export const signinWindow = () => {
    const state = uuid.v4();
    const url = oauthRequest(state);
    return {
        type: SIGNIN_WINDOW_OPEN,
        payload: { state, url },
    };
};

export const closeSigninWindow = () => ({
    type: SIGNIN_WINDOW_CLOSE,
});

export const signinWindowClosed = () => ({
    type: SIGNIN_WINDOW_CLOSED,
});

export const signinIframe = () => {
    const state = uuid.v4();
    const url = oauthRequest(state);
    return {
        type: SIGNIN_IFRAME_REQUEST,
        payload: { state, url },
    };
};

export const signinIframeLoaded = () => ({
    type: SIGNIN_IFRAME_RESPONSE,
});
