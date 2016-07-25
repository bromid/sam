import {
    LOGIN_REQUEST,
    LOGOUT_REQUEST,
} from '../constants';

export const login = () => ({
    type: LOGIN_REQUEST,
});

export const logout = () => ({
    type: LOGOUT_REQUEST,
});
