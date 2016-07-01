import React, { PropTypes } from 'react';
import { Provider } from 'react-redux';
import { Router, Route, browserHistory } from 'react-router';
import App from './App';
import Groups from './Groups';
import Applications from './Applications';
import Servers from './Servers';
import Server from './Server';

const Root = ({ store }) => (
    <Provider store={store}>
        <Router history={browserHistory}>
            <Route path="/" component={App} >
                <Route path="groups" component={Groups} />
                <Route path="applications" component={Applications} />
                <Route path="servers" component={Servers} />
                <Route path="servers/:environment/:hostname" component={Server} />
            </Route>
        </Router>
    </Provider>
);

Root.propTypes = {
    store: PropTypes.object.isRequired,
};

export default Root;
