import React from 'react';
import { Link } from 'react-router';
import { List, ListItem } from 'material-ui/List';

const DeploymentListItem = ({ deployment: { applicationLink }, deployment }) => {
    const id = applicationLink.id;
    const name = (applicationLink.name) ? applicationLink.name : `(${id})`;
    return (
        <Link to={`/application/${id}`}>
            <ListItem primaryText={`${name} (${deployment.version})`} />
        </Link>
    );
};

export const DeploymentList = ({ deployments }) => {
    if (!deployments) return <p>No deployments</p>;
    return (
        <List>
            {deployments.map((deployment) => (
                <DeploymentListItem key={deployment.applicationLink.id} deployment={deployment} />
            ))}
        </List>
    );
};
