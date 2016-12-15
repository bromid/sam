import { combineReducers } from 'redux';
import {
    SIGNIN_REQUEST,
    SIGNIN_RESPONSE,
    SIGNOUT_RESPONSE,
    SIGNIN_IFRAME_REQUEST,
    SIGNIN_IFRAME_RESPONSE,
    SIGNIN_WINDOW_OPEN,
    SIGNIN_WINDOW_CLOSE,
} from '../constants';
import isEmpty from 'lodash/isEmpty';

const user = (state = null, action) => {
    switch (action.type) {
        case SIGNIN_RESPONSE:
            return action.user;
        case SIGNOUT_RESPONSE:
            return null;
        default:
            return state;
    }
};

const isPending = (state = false, action) => {
    switch (action.type) {
        case SIGNIN_REQUEST:
            return true;
        case SIGNIN_RESPONSE:
            return false;
        default:
            return state;
    }
};

const signinWindowRequest = (state = null, action) => {
    switch (action.type) {
        case SIGNIN_WINDOW_OPEN:
            return action.payload;
        case SIGNIN_WINDOW_CLOSE:
            return null;
        default:
            return state;
    }
};

const signinIframeRequest = (state = null, action) => {
    switch (action.type) {
        case SIGNIN_IFRAME_REQUEST:
            return action.payload;
        case SIGNIN_IFRAME_RESPONSE:
            return null;
        default:
            return state;
    }
};

export const fromAuth = {
    getIsPending: (state) => state.isPending,
    getIsAuthenticated: (state) => !isEmpty(state.user),
    getAuthenticatedUser: (state) => state.user,
    getSigninWindowRequest: (state) => state.signinWindowRequest,
    getSigninIframeRequest: (state) => state.signinIframeRequest,
};

export default combineReducers({
    user,
    isPending,
    signinWindowRequest,
    signinIframeRequest,
});
