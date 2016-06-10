import { OPEN_MENU, CLOSE_MENU } from '../constants';

export const menuOpen = (state = false, action) => {
    switch (action.type) {
        case OPEN_MENU:
            return true;
        case CLOSE_MENU:
            return false;
        default:
            return state;
    }
};
