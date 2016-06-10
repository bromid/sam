import 'babel-polyfill';
import injectTapEventPlugin from 'react-tap-event-plugin';
import React from 'react';
import { render } from 'react-dom';
import Root from './components/Root';
import configureStore from './configureStore';

injectTapEventPlugin();

const store = configureStore();
render(
    <Root store={store} />,
    document.getElementById('root')
);
