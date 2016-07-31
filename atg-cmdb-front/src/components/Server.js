import React from 'react';
import { Link } from 'react-router';
import { connect } from 'react-redux';
import size from 'lodash/size';
import isEmpty from 'lodash/isEmpty';
import * as serverActions from '../actions/serverActions';
import * as metaActions from '../actions/metaActions';
import { List, ListItem } from 'material-ui/List';
import { flexWrapperStyle } from '../style';
import LoadingIndicator from './LoadingIndicator';
import Attributes from './Attributes';
import ItemView from './ItemView';
import { fromServer, getIsMetaOpen } from '../reducers';

const flexChildStyle = {
    minWidth: 250,
    padding: '0 16px',
};

const collectionSize = (collection) => {
    if (!collection) return ' (0)';
    return ` (${size(collection)})`;
};

export const serverName = (server) => (
    `${server.hostname}@${server.environment}`
);

export const serverLink = (server) => (
    `/server/${server.environment}/${server.hostname}`
);

const patchNotification = (result, error, isPending) => {
    if (isPending) return {};
    if (!isEmpty(error)) {
        return {
            message: 'Failed to update server!',
            duration: 4000,
            action: {
                name: 'info',
            },
        };
    }
    if (!isEmpty(result)) {
        return {
            message: `Updated server ${result.hostname}@${result.environment}`,
        };
    }
    return {};
};

const Os = ({ os }) => (
    <div style={flexChildStyle}>
        <h3>Operative system</h3>
        <p>{os.name} ({os.version})</p>
        <h4>Attributes</h4>
        <Attributes attributes={os.attributes} />
    </div>
);

const Network = ({ network }) => (
    <div style={flexChildStyle}>
        <h3>Network</h3>
        <p>{network.ipv4Address}</p>
        <h4>Attributes</h4>
        <Attributes attributes={network.attributes} />
    </div>
);

const Deployment = ({ deployment: { applicationLink }, deployment }) => (
    <Link to={`/application/${applicationLink.id}`}>
        <ListItem primaryText={`${applicationLink.name} (${deployment.version})`} />
    </Link>
);

const DeploymentList = ({ deployments }) => {
    if (!deployments) return <p>No deployments</p>;
    return (
        <List>
            {deployments.map((deployment) => (
                <Deployment key={deployment.applicationLink.id} deployment={deployment} />
            ))}
        </List>
    );
};

const ServerContainer = React.createClass({

    updateDescription(description) {
        const { patchServer, server: { hostname, environment, meta } } = this.props;
        patchServer(hostname, environment, { description }, { hash: meta.hash });
    },

    render() {
        const {
            isLoading, server,
            metaOpen, toggleMeta,
            patchResult, patchError, patchIsPending,
        } = this.props;

        if (isLoading && isEmpty(server)) return <LoadingIndicator />;
        if (!server.hostname) return <p>No result</p>;

        const { description = '', meta, network, os, deployments, attributes } = server;

        const tabs = [
            {
                name: 'Details',
                node: (
                    <div style={{ ...flexWrapperStyle, margin: '16px 0' }}>
                        <Os os={os} />
                        <Network network={network} />
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
        return (
            <ItemView
                headline={serverName(server)}
                description={description}
                updateDescription={this.updateDescription}
                meta={meta}
                metaOpen={metaOpen}
                toggleMeta={toggleMeta}
                tabs={tabs}
                notification={() => patchNotification(patchResult, patchError, patchIsPending)}
                isLoading={isLoading}
            />
        );
    },
});

const mapStateToProps = (state) => ({
    metaOpen: getIsMetaOpen(state),
    server: fromServer.getCurrent(state),
    fetchError: fromServer.getCurrentError(state),
    patchResult: fromServer.getPatchResult(state),
    patchError: fromServer.getPatchResultError(state),
    patchIsPending: fromServer.getPatchResultIsPending(state),
    isLoading: fromServer.getCurrentIsPending(state),
});


const Actions = {
    patchServer: serverActions.patchServer,
    toggleMeta: metaActions.toggleMeta,
};
export default connect(mapStateToProps, Actions)(ServerContainer);
