import React, { PropTypes } from 'react';
import { Provider } from 'react-redux';
import { Router, Route, browserHistory } from 'react-router';
import App from './App';
import Groups from './GroupList';
import Group from './Group';
import Applications from './ApplicationList';
import Application from './Application';
import Servers from './ServerList';
import Server from './Server';
import Assets from './AssetList';
import Asset from './Asset';
import ReleaseNotes from './ReleaseNotes';

const Root = ({ store }) => (
    <Provider store={store}>
        <Router history={browserHistory}>
            <Route path="/" component={App} >
                <Route path="group" component={Groups} />
                <Route path="group/:id" component={Group} />
                <Route path="application" component={Applications} />
                <Route path="application/:id" component={Application} />
                <Route path="server" component={Servers} />
                <Route path="server/:environment" component={Servers} />
                <Route path="server/:environment/:hostname" component={Server} />
                <Route path="asset" component={Assets} />
                <Route path="asset/:id" component={Asset} />
                <Route path="release-notes" component={ReleaseNotes} />
            </Route>
        </Router>
    </Provider>
);

Root.propTypes = {
    store: PropTypes.object.isRequired,
};

export default Root;
