import React from 'react';
import { Link } from 'react-router';
import { connect } from 'react-redux';
import groupBy from 'lodash/groupBy';
import keys from 'lodash/keys';
import capitalize from 'lodash/capitalize';
import { collectionSize } from '../helpers';
import { List, ListItem } from 'material-ui/List';
import LoadingIndicator from './LoadingIndicator';
import { serverName, serverLink } from './ServerList';
import { fromApplication } from '../reducers';

const ServerListItem = ({ deployment }) => (
    <Link to={serverLink(deployment)}>
        <ListItem primaryText={`${serverName(deployment)}`} nestedLevel={1} />
    </Link>
);

const EnvironmentAndVersionListItem = ({ deployments }) => {
    const environment = deployments[0].environment;
    const version = deployments[0].version;
    return (
        <ListItem
            primaryText={`${capitalize(environment)} ${collectionSize(deployments)}`}
            secondaryText={`Version: ${version}`}
            primaryTogglesNestedList={true}
            nestedItems={deployments.map((deployment) => (
                <ServerListItem key={deployment.hostname} deployment={deployment} />
            ))}
        />
    );
};

const DeploymentList = ({ deployments }) => {
    if (!deployments) return <p>No deployments</p>;

    const byVersionAndEnvironment = groupBy(deployments, (deployment) => (
        `${deployment.environment} ${deployment.version}`
    ));
    return (
        <List>
            {keys(byVersionAndEnvironment).map((key) => (
                <EnvironmentAndVersionListItem
                    key={key}
                    deployments={byVersionAndEnvironment[key]}
                />
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
