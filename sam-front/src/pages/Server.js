import React from 'react';
import { connect } from 'react-redux';
import size from 'lodash/size';
import isEmpty from 'lodash/isEmpty';
import * as serverActions from '../actions/serverActions';
import { flexWrapperStyle } from '../style';
import LoadingIndicator from '../components/LoadingIndicator';
import Attributes from '../components/Attributes';
import ItemView from '../components/ItemView';
import { DeploymentList } from '../components/ServerDeployment';
import { serverName } from '../components/ServerList';
import RefreshButton from '../components/ItemView/RefreshButton';
import DeleteButton from '../components/ItemView/DeleteButton';
import { fromServer, fromAuth } from '../reducers';

const flexChildStyle = {
    minWidth: 250,
    padding: '0 16px',
};

const collectionSize = (collection) => {
    if (!collection) return ' (0)';
    return ` (${size(collection)})`;
};

const General = ({ server }) => (
    <div style={flexChildStyle}>
        <dl style={{ margin: 0 }}>
            <dt>Qualified domain name</dt>
            <dd>{server.fqdn}</dd>
        </dl>
    </div>
);

const Os = ({ os }) => (
    <div style={flexChildStyle}>
        <h3>Operative system</h3>
        <p>{os.name} ({os.version})</p>
        <Attributes attributes={os.attributes} search={false} />
    </div>
);

const Network = ({ network }) => (
    <div style={flexChildStyle}>
        <h3>Network</h3>
        <p>{network.ipv4Address}</p>
        <Attributes attributes={network.attributes} search={false} />
    </div>
);

const ServerContainer = React.createClass({

    updateDescription(description) {
        const { patchServer, server: { hostname, environment, meta } } = this.props;
        patchServer(hostname, environment, { description }, { hash: meta.hash });
    },

    render() {
        const {
            server, isAuthenticated, isLoading, patchIsPending, patchError,
            fetchServer, deleteServer,
        } = this.props;

        if (isLoading && isEmpty(server)) return <LoadingIndicator />;
        if (!server.hostname) return <p>No result</p>;

        const {
            hostname, environment, description = '', meta, network, os, deployments, attributes,
        } = server;

        const tabs = [
            {
                name: 'Details',
                node: (
                    <div style={{ ...flexWrapperStyle, margin: '16px 0' }}>
                        <General server={server} />
                        {os && <Os os={os} />}
                        {network && <Network network={network} />}
                    </div>
                ),
            },
            {
                name: `Deployments ${collectionSize(deployments)}`,
                node: <DeploymentList deployments={deployments} />,
            },
            {
                name: `Attributes ${collectionSize(attributes)}`,
                node: <Attributes attributes={attributes} />,
            },
        ];

        const onRefresh = () => fetchServer(hostname, environment);
        const onDelete = () => deleteServer(hostname, environment);
        const buttons = [<RefreshButton key="refresh" onClick={onRefresh} />];
        if (isAuthenticated) {
            buttons.push(<DeleteButton key="delete" onClick={onDelete} />);
        }

        return (
            <ItemView
                headline={serverName(server)}
                description={description}
                updateDescription={this.updateDescription}
                meta={meta}
                tabs={tabs}
                isLoading={isLoading}
                patchIsPending={patchIsPending}
                patchError={patchError}
                buttons={buttons}
            />
        );
    },
});

const mapStateToProps = (state) => ({
    server: fromServer.getCurrent(state),
    fetchError: fromServer.getCurrentError(state),
    patchResult: fromServer.getPatchResult(state),
    patchError: fromServer.getPatchResultError(state),
    patchIsPending: fromServer.getPatchResultIsPending(state),
    isLoading: fromServer.getCurrentIsPending(state),
    isAuthenticated: fromAuth.getIsAuthenticated(state),
});

const Actions = {
    patchServer: serverActions.patchServer,
    fetchServer: serverActions.fetchServer,
    deleteServer: serverActions.deleteServer,
};
export default connect(mapStateToProps, Actions)(ServerContainer);
