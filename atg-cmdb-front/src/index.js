import 'babel-polyfill';
import injectTapEventPlugin from 'react-tap-event-plugin';
import React from 'react';
import { render } from 'react-dom';
import Root from './pages/Root';

injectTapEventPlugin();

render(
    <Root />,
    document.getElementById('root')
);
