import { fork } from 'redux-saga/effects';
import applicationSagas from './applicationSagas';
import assetSagas from './assetSagas';
import authSagas from './authSagas';

export default function* rootSaga() {
    yield fork(applicationSagas);
    yield fork(assetSagas);
    yield fork(authSagas);
}
