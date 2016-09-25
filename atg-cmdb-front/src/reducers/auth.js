import { combineReducers } from 'redux';
import {
    LOGIN_REQUEST,
    LOGIN_RESPONSE,
    LOGOUT_RESPONSE,
} from '../constants';
import isEmpty from 'lodash/isEmpty';

const user = (state = null, action) => {
    switch (action.type) {
        case LOGIN_RESPONSE:
            return action.user;
        case LOGOUT_RESPONSE:
            return null;
        default:
            return state;
    }
};

const isPending = (state = false, action) => {
    switch (action.type) {
        case LOGIN_REQUEST:
            return true;
        case LOGIN_RESPONSE:
            return false;
        default:
            return state;
    }
};

export const fromAuth = {
    getIsPending: (state) => state.isPending,
    getIsAuthenticated: (state) => !isEmpty(state.user),
    getAuthenticatedUser: (state) => state.user,
};

export default combineReducers({
    user,
    isPending,
});
