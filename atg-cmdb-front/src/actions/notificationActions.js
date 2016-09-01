import uuid from 'node-uuid';
import {
    NOTIFICATION,
} from '../constants';

export const showNotification = (message) => ({
    type: NOTIFICATION,
    payload: {
        id: uuid.v4(),
        message,
    },
});

export const showErrorNotification = (message, error) => ({
    type: NOTIFICATION,
    payload: {
        id: uuid.v4(),
        duration: 7000,
        action: {
            name: 'info',
            onTouchTap: (showDialog) => showDialog(error.message, message),
        },
        message,
    },
});
