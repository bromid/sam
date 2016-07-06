import React from 'react';
import { Link } from 'react-router';
import { connect } from 'react-redux';
import * as Actions from '../actions/serverActions';
import { List, ListItem } from 'material-ui/List';
import LoadingIndicator from './LoadingIndicator';
import Attributes from './Attributes';

function Os({ os }) {
    return (
        <div>
            <h3>{os.type}</h3>
            <p>{os.name} ({os.version})</p>
            <Attributes attributes={os.attributes} />
        </div>
    );
}

function Network({ network }) {
    return (
        <div>
            <h3>Network</h3>
            <p>{network.ipv4Address}</p>
            <Attributes attributes={network.attributes} />
        </div>
    );
}

function DeploymentList({ deployments }) {
    if (!deployments) {
        return (
            <div>
                <h3>Deployments</h3>
                <p>No deployments</p>
            </div>
        );
    }
    return (
        <List>
            <h3>Deployments</h3>
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
        const { server, isLoading } = this.props;
        if (isLoading) return <LoadingIndicator />;
        if (!server.hostname) return <p>No results</p>;
        return (
            <div style={{ padding: '8px 0' }}>
                <h2>{server.hostname}@{server.environment}</h2>
                <div style={{ margin: 16 }}>
                    <p>{server.description}</p>
                    <Attributes attributes={server.attributes} />
                    <DeploymentList deployments={server.deployments} />
                    <Os os={server.os} />
                    <Network network={server.network} />
                </div>
            </div>
        );
    },
});

function mapStateToProps(state, props) {
    const { server, serverIsLoading } = state;
    const { params } = props;
    return {
        server,
        environment: params.environment,
        hostname: params.hostname,
        isLoading: serverIsLoading || serverIsLoading === null,
    };
}
export default connect(mapStateToProps, Actions)(ServerContainer);
