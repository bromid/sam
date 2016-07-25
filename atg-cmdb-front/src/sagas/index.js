import { fork } from 'redux-saga/effects';
import applicationSagas from './applicationSagas';
import assetSagas from './assetSagas';

export default function* rootSaga() {
    yield fork(applicationSagas);
    yield fork(assetSagas);
}
