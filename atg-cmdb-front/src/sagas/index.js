import { fork } from 'redux-saga/effects';
import applicationSagas from './applicationSagas';

export default function* rootSaga() {
    yield fork(applicationSagas);
}
