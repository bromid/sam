import React from 'react';
import { Link } from 'react-router';
import { List, ListItem } from 'material-ui/List';

const ApplicationListItem = ({ application: { id, name, description } }) => (
    <Link to={`/application/${id}`}>
        <ListItem primaryText={name} secondaryText={description} />
    </Link>
);

export const ApplicationList = ({ applications }) => {
    if (!applications) return <p>No applications</p>;
    return (
        <List>
            {applications.map((application) => (
                <ApplicationListItem key={application.id} application={application} />
            ))}
        </List>
    );
};
