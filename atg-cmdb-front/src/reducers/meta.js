import { OPEN_META, CLOSE_META } from '../constants';

export const metaOpen = (state = true, action) => {
    switch (action.type) {
        case OPEN_META:
            return true;
        case CLOSE_META:
            return false;
        default:
            return state;
    }
};
