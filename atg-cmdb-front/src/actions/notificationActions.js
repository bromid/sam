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
        duration: 5000,
        action: {
            name: 'info',
        },
        message: `${message}, Error: ${error.message}`,
    },
});
