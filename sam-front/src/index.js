import 'babel-polyfill';
import injectTapEventPlugin from 'react-tap-event-plugin';
import React from 'react';
import { render } from 'react-dom';
import Root from './pages/Root';

injectTapEventPlugin();

if (module.hot) {
    module.hot.accept('./pages/Root', () => {
        const NextRoot = require('./pages/Root').default; // eslint-disable-line global-require
        render(
            <NextRoot />,
            document.getElementById('root')
        );
    });
}

render(
    <Root />,
    document.getElementById('root')
);
