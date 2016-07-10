import React from 'react';
import { Link } from 'react-router';
import { connect } from 'react-redux';
import size from 'lodash/size';
import * as serverActions from '../actions/serverActions';
import * as metaActions from '../actions/metaActions';
import { List, ListItem } from 'material-ui/List';
import LoadingIndicator from './LoadingIndicator';
import Attributes from './Attributes';
import ItemView from './ItemView';

const flexWrapper = {
    display: 'flex',
    flexFlow: 'row wrap',
    justifyContent: 'flex-start',
    alignItems: 'stretch',
};

const flexChildStyle = {
    minWidth: 250,
    padding: '0 16px',
};

function collectionSize(collection) {
    if (!collection) return ' (0)';
    return ` (${size(collection)})`;
}

function Os({ os }) {
    return (
        <div style={flexChildStyle}>
            <h3>Operativ system</h3>
            <p>{os.name} ({os.version})</p>
            <h4>Attributes</h4>
            <Attributes attributes={os.attributes} />
        </div>
    );
}

function Network({ network }) {
    return (
        <div style={flexChildStyle}>
            <h3>Network</h3>
            <p>{network.ipv4Address}</p>
            <h4>Attributes</h4>
            <Attributes attributes={network.attributes} />
        </div>
    );
}

function DeploymentList({ deployments }) {
    if (!deployments) return <p>No deployments</p>;

    return (
        <List>
            {deployments.map(deployment => (
                <Deployment key={deployment.applicationLink.id} deployment={deployment} />
            ))}
        </List>
    );
}

function Deployment({ deployment }) {
    return (
        <ListItem
            primaryText={
                <Link to={`/application/${deployment.applicationLink.id}`}>
                    {`${deployment.applicationLink.name} (${deployment.version})`}
                </Link>
            }
        />
    );
}

const ServerContainer = React.createClass({

    componentDidMount() {
        const { environment, hostname, fetchServer } = this.props;
        fetchServer({ environment, hostname });
    },

    componentWillReceiveProps(newProps) {
        const { environment, hostname, fetchServer } = this.props;
        const {
            environment: newEnvironment,
            hostname: newHostname,
        } = newProps;

        if (newEnvironment !== environment || newHostname !== hostname) {
            fetchServer({
                environment: newEnvironment,
                hostname: newHostname,
            });
        }
    },

    render() {
        const {
            isLoading,
            metaOpen,
            toggleMeta,
            server: {
                hostname, environment, description,
                meta, network, os, deployments, attributes,
            },
        } = this.props;
        if (isLoading) return <LoadingIndicator />;
        if (!hostname) return <p>No result</p>;

        const tabs = [
            {
                name: 'Details',
                node: (
                    <div style={{ ...flexWrapper, margin: '16px 0' }}>
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
                meta={meta}
                metaOpen={metaOpen}
                toggleMeta={toggleMeta}
                tabs={tabs}
            />
        );
    },
});

function mapStateToProps(state, props) {
    const { metaOpen, server, serverIsLoading } = state;
    const { environment, hostname } = props.params;
    return {
        environment,
        hostname,
        metaOpen,
        server,
        isLoading: serverIsLoading || serverIsLoading === null,
    };
}

const Actions = { ...serverActions, ...metaActions };
export default connect(mapStateToProps, Actions)(ServerContainer);
