import { fork } from 'redux-saga/effects';
import infoSagas from './infoSagas';
import authSagas from './authSagas';
import searchSagas from './searchSagas';
import groupSagas from './groupSagas';
import applicationSagas from './applicationSagas';
import serverSagas from './serverSagas';
import assetSagas from './assetSagas';

export default function* rootSaga() {
    yield fork(infoSagas);
    yield fork(authSagas);
    yield fork(searchSagas);
    yield fork(groupSagas);
    yield fork(applicationSagas);
    yield fork(serverSagas);
    yield fork(assetSagas);
}
