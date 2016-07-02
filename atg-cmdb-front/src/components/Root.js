import React, { PropTypes } from 'react';
import { Provider } from 'react-redux';
import { Router, Route, browserHistory } from 'react-router';
import App from './App';
import Groups from './GroupList';
import Applications from './ApplicationList';
import Servers from './ServerList';
import Server from './Server';

const Root = ({ store }) => (
    <Provider store={store}>
        <Router history={browserHistory}>
            <Route path="/" component={App} >
                <Route path="group" component={Groups} />
                <Route path="group/:id" component={Groups} />
                <Route path="application" component={Applications} />
                <Route path="application/:id" component={Applications} />
                <Route path="server" component={Servers} />
                <Route path="server/:environment" component={Servers} />
                <Route path="server/:environment/:hostname" component={Server} />
            </Route>
        </Router>
    </Provider>
);

Root.propTypes = {
    store: PropTypes.object.isRequired,
};

export default Root;
