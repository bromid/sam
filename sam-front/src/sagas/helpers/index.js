import { take, race } from 'redux-saga/effects';
import { delay } from 'redux-saga';
import isString from 'lodash/isString';

const resolveRaceAction = (action) => {
    if (isString(action)) return take(action);
    return action;
};

export function* timeout(action, ms) {
    return yield race({
        response: resolveRaceAction(action),
        cancelled: delay(ms),
    });
}
