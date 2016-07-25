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

    getInitialState() {
        return { initiated: false };
    },

    componentDidMount() {
        const { environment, hostname, fetchServer } = this.props;
        fetchServer({ environment, hostname });
    },

    componentWillReceiveProps(newProps) {
        const { environment, hostname, patchResult, fetchServer } = this.props;
        const {
            environment: newEnvironment,
            hostname: newHostname,
            patchResult: newPatchResult,
        } = newProps;

        const isDifferentEtag = newPatchResult.etag !== patchResult.etag;
        const isUpdatedEtag = !isEmpty(newPatchResult) && isDifferentEtag;
        if (newEnvironment !== environment || newHostname !== hostname || isUpdatedEtag) {
            this.setState({ initiated: true });
            fetchServer({
                environment: newEnvironment,
                hostname: newHostname,
            });
        }
    },

    updateDescription(description) {
        const { environment, hostname, patchServer, server: { meta } } = this.props;
        patchServer({ environment, hostname }, { description }, {
            hash: meta.hash,
        });
    },

    render() {
        const {
            isLoading,
            metaOpen, toggleMeta,
            patchResult, patchError, patchIsPending,
            server: {
                hostname, environment, description = '',
                meta, network, os, deployments, attributes,
            },
        } = this.props;
        if (isLoading && !this.state.initiated) return <LoadingIndicator />;
        if (!hostname) return <p>No result</p>;

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
                headline={`${hostname}@${environment}`}
                description={description}
                updateDescription={this.updateDescription}
                meta={meta}
                metaOpen={metaOpen}
                toggleMeta={toggleMeta}
                tabs={tabs}
                notification={() => patchNotification(patchResult, patchError, patchIsPending)}
            />
        );
    },
});

function mapStateToProps(state, props) {
    const {
        metaOpen,
        server, serverError, serverIsPending,
        serverPatchResult, serverPatchResultError, serverPatchResultIsPending,
    } = state;
    const { environment, hostname } = props.params;
    return {
        environment,
        hostname,
        metaOpen,
        server,
        fetchError: serverError,
        patchResult: serverPatchResult,
        patchError: serverPatchResultError,
        patchIsPending: serverPatchResultIsPending,
        isLoading: serverIsPending || serverIsPending === null,
    };
}

const Actions = { ...serverActions, ...metaActions };
export default connect(mapStateToProps, Actions)(ServerContainer);
