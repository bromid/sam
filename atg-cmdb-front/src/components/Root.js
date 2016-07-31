import React, { PropTypes } from 'react';
import { Provider } from 'react-redux';
import { Router, Route, browserHistory } from 'react-router';
import configureStore from '../configureStore';
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
import * as ApplicationActions from '../actions/applicationActions';
import * as AssetActions from '../actions/assetActions';
import * as GroupActions from '../actions/groupActions';
import * as ServerActions from '../actions/serverActions';
import * as InfoActions from '../actions/infoActions';

export const store = configureStore();

const Root = () => {
    const initApp = () =>
        store.dispatch(InfoActions.fetchInfo());

    const fetchGroupList = ({ location: { query } }) => {
        store.dispatch(GroupActions.fetchGroupList(query.tags));
        store.dispatch(GroupActions.fetchGroupTags());
    };

    const fetchGroup = ({ params }) =>
        store.dispatch(GroupActions.fetchGroup(params.id));

    const fetchApplicationList = () =>
        store.dispatch(ApplicationActions.fetchApplicationList());

    const fetchApplication = ({ params }) =>
        store.dispatch(ApplicationActions.fetchApplication(params.id));

    const fetchServerList = ({ params }) =>
        store.dispatch(ServerActions.fetchServerList(params.environment));

    const fetchServer = ({ params }) =>
        store.dispatch(ServerActions.fetchServer(params.hostname, params.environment));

    const fetchAssetList = () =>
        store.dispatch(AssetActions.fetchAssetList());

    const fetchAsset = ({ params }) =>
        store.dispatch(AssetActions.fetchAsset(params.id));

    const fetchReleaseNotes = () =>
        store.dispatch(InfoActions.fetchReleaseNotes());

    return (
        <Provider store={store}>
            <Router history={browserHistory}>
                <Route
                    path="/"
                    component={App}
                    onEnter={initApp}
                >
                    <Route
                        path="group"
                        component={Groups}
                        onEnter={fetchGroupList}
                        onChange={(state, nextState) => fetchGroupList(nextState)}
                    />
                    <Route
                        path="group/:id"
                        component={Group}
                        onEnter={fetchGroup}
                    />
                    <Route
                        path="application"
                        component={Applications}
                        onEnter={fetchApplicationList}
                    />
                    <Route
                        path="application/:id"
                        component={Application}
                        onEnter={fetchApplication}
                    />
                    <Route
                        path="server"
                        component={Servers}
                        onEnter={fetchServerList}
                    />
                    <Route
                        path="server/:environment"
                        component={Servers}
                        onEnter={fetchServerList}
                    />
                    <Route
                        path="server/:environment/:hostname"
                        component={Server}
                        onEnter={fetchServer}
                    />
                    <Route
                        path="asset"
                        component={Assets}
                        onEnter={fetchAssetList}
                    />
                    <Route
                        path="asset/:id"
                        component={Asset}
                        onEnter={fetchAsset}
                    />
                    <Route
                        path="release-notes"
                        component={ReleaseNotes}
                        onEnter={fetchReleaseNotes}
                    />
                </Route>
            </Router>
        </Provider>
    );
};
export default Root;
