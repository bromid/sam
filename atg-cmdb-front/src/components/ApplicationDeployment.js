import React from 'react';
import { Link } from 'react-router';
import { connect } from 'react-redux';
import { List, ListItem } from 'material-ui/List';
import LoadingIndicator from './LoadingIndicator';
import { serverName, serverLink } from './ServerList';
import { fromApplication } from '../reducers';

const DeploymentListItem = ({ deployment }) => (
    <Link to={serverLink(deployment)}>
        <ListItem primaryText={`${serverName(deployment)} (${deployment.version})`} />
    </Link>
);

export const DeploymentList = ({ deployments, header }) => {
    if (!deployments) return <p>No deployments</p>;
    return (
        <List>
            {header}
            {deployments.map((deployment) => (
                <DeploymentListItem key={serverName(deployment)} deployment={deployment} />
            ))}
        </List>
    );
};

const DeploymentListContainer = ({ isLoading, deployments }) => {
    if (isLoading) return <LoadingIndicator />;
    return (
        <DeploymentList deployments={deployments} />
    );
};

const mapStateToProps = (state) => ({
    deployments: fromApplication.getDeployments(state),
    isLoading: fromApplication.getDeploymentsIsPending(state),
});
export default connect(mapStateToProps)(DeploymentListContainer);
