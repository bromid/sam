import {
    LOGIN_RESPONSE,
    LOGOUT_RESPONSE,
} from '../constants';

export default function authenticated(state = null, action) {
    switch (action.type) {
        case LOGIN_RESPONSE:
            return action.user;
        case LOGOUT_RESPONSE:
            return null;
        default:
            return state;
    }
}
