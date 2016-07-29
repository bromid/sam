import {
    NOTIFICATION,
} from '../constants';

export default function notification(state = {}, action) {
    switch (action.type) {
        case NOTIFICATION:
            return action.payload;
        default:
            return state;
    }
}
