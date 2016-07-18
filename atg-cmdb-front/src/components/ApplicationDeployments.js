import React from 'react';
import { Link } from 'react-router';
import { connect } from 'react-redux';
import { List, ListItem } from 'material-ui/List';
import * as applicationActions from '../actions/applicationActions';
import LoadingIndicator from './LoadingIndicator';

function serverName(deployment, separator = '@') {
    return deployment.hostname + separator + deployment.environment;
}

function serverLink(deployment) {
    return `/server/${deployment.environment}/${deployment.hostname}`;
}

function DeploymentList({ deployments }) {
    if (!deployments) return <p>No deployments</p>;

    return (
        <List>
            {deployments.map(deployment => (
                <Deployment key={serverName(deployment)} deployment={deployment} />
            ))}
        </List>
    );
}

function Deployment({ deployment }) {
    return (
        <ListItem
            primaryText={
                <Link to={serverLink(deployment)}>
                    {`${serverName(deployment)} (${deployment.version})`}
                </Link>
            }
        />
    );
}

const ApplicationDeploymentsContainer = React.createClass({

    componentDidMount() {
        const { id, fetchApplicationDeployments } = this.props;
        fetchApplicationDeployments(id);
    },

    render() {
        const {
            isLoading,
            deployments,
        } = this.props;
        if (isLoading) return <LoadingIndicator />;

        return (
            <DeploymentList deployments={deployments} />
        );
    },
});

function mapStateToProps(state) {
    const { applicationDeployments, applicationDeploymentsIsPending } = state;
    return {
        deployments: applicationDeployments.items,
        isLoading: applicationDeploymentsIsPending || applicationDeploymentsIsPending === null,
    };
}

const Actions = { ...applicationActions };
export default connect(mapStateToProps, Actions)(ApplicationDeploymentsContainer);
