import React from 'react';
import { Provider } from 'react-redux';
import { Router, Route, browserHistory } from 'react-router';
import configureStore from '../configureStore';
import App from './App';
import Group from './Group';
import GroupList from './GroupList';
import NewGroup from './NewGroup';
import Application from './Application';
import ApplicationList from './ApplicationList';
import NewApplication from './NewApplication';
import Server from './Server';
import ServerList from './ServerList';
import Asset from './Asset';
import AssetList from './AssetList';
import NewAsset from './NewAsset';
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

    const fetchGroupIds = () =>
        store.dispatch(GroupActions.fetchGroupIds());

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
                        component={GroupList}
                        onEnter={fetchGroupList}
                        onChange={(state, nextState) => fetchGroupList(nextState)}
                    />
                    <Route
                        path="group/new"
                        component={NewGroup}
                    />
                    <Route
                        path="group/:id"
                        component={Group}
                        onEnter={fetchGroup}
                    />
                    <Route
                        path="application"
                        component={ApplicationList}
                        onEnter={fetchApplicationList}
                    />
                    <Route
                        path="application/new"
                        component={NewApplication}
                        onEnter={fetchGroupIds}
                    />
                    <Route
                        path="application/:id"
                        component={Application}
                        onEnter={fetchApplication}
                    />
                    <Route
                        path="server"
                        component={ServerList}
                        onEnter={fetchServerList}
                    />
                    <Route
                        path="server/:environment"
                        component={ServerList}
                        onEnter={fetchServerList}
                    />
                    <Route
                        path="server/:environment/:hostname"
                        component={Server}
                        onEnter={fetchServer}
                    />
                    <Route
                        path="asset"
                        component={AssetList}
                        onEnter={fetchAssetList}
                    />
                    <Route
                        path="asset/new"
                        component={NewAsset}
                        onEnter={fetchGroupIds}
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
