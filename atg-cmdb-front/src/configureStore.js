import { createStore, applyMiddleware, compose } from 'redux';
import createLogger from 'redux-logger';
import thunk from 'redux-thunk';
import createSagaMiddleware from 'redux-saga';
import reducers from './reducers';
import rootSaga from './sagas';

const sagaMiddleware = createSagaMiddleware();

const configureStore = () => {
    const middlewares = [thunk, sagaMiddleware];
    if (process.env.NODE_ENV !== 'production') {
        middlewares.push(createLogger());
    }

    const store = createStore(
        reducers,
        compose(
            applyMiddleware(...middlewares),
            window.devToolsExtension ? window.devToolsExtension() : (f) => f
        )
    );

    if ((process.env.NODE_ENV !== 'production')) {
        window.store = store;
    }

    sagaMiddleware.run(rootSaga);

    return store;
};

export default configureStore;
