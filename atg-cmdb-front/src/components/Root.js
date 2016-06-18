import React, { PropTypes } from 'react';
import { Provider } from 'react-redux';
import { Router, Route, browserHistory } from 'react-router';
import icon1 from '../icons/favicon.ico';
import icon2 from '../icons/favicon-16x16.png';
import icon3 from '../icons/favicon-32x32.png';
import icon4 from '../icons/favicon-96x96.png';
import icon5 from '../icons/favicon-160x160.png';
import icon6 from '../icons/favicon-192x192.png';
import App from './App';
import Groups from './Groups';
import Applications from './Applications';

const Root = ({ store }) => (
    <Provider store={store}>
        <Router history={browserHistory}>
            <Route path="/" component={App} >
                <Route path="groups" component={Groups} />
                <Route path="applications" component={Applications} />
            </Route>
        </Router>
    </Provider>
);

Root.propTypes = {
    store: PropTypes.object.isRequired,
};

export default Root;
