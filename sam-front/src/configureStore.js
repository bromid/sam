import { createStore, applyMiddleware, compose } from 'redux';
import createLogger from 'redux-logger';
import thunk from 'redux-thunk';
import createSagaMiddleware from 'redux-saga';
import throttle from 'lodash/throttle';
import reducers from './reducers';
import rootSaga from './sagas';

const sagaMiddleware = createSagaMiddleware();

const saveState = (state) => {
    try {
        const serializedState = JSON.stringify(state);
        localStorage.setItem('state', serializedState);
    } catch (err) {
        // Ignore write errors
    }
};

const loadState = () => {
    try {
        const serializedState = localStorage.getItem('state');
        if (serializedState === null) {
            return undefined;
        }
        return JSON.parse(serializedState);
    } catch (err) {
        return undefined;
    }
};

const configureStore = () => {
    const middlewares = [thunk, sagaMiddleware];
    if (process.env.NODE_ENV !== 'production') {
        middlewares.push(createLogger());
    }

    const store = createStore(
        reducers,
        loadState(),
        compose(
            applyMiddleware(...middlewares),
            window.devToolsExtension ? window.devToolsExtension() : (f) => f
        )
    );

    if ((process.env.NODE_ENV !== 'production')) {
        window.store = store;
    }

    store.subscribe(throttle(() => {
        saveState({
            metaOpen: store.getState().metaOpen,
        });
    }, 1000));

    sagaMiddleware.run(rootSaga);

    return store;
};

export default configureStore;
