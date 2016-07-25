import React from 'react';
import { Link } from 'react-router';
import { connect } from 'react-redux';
import { List, ListItem } from 'material-ui/List';
import LoadingIndicator from './LoadingIndicator';

const Application = ({ application: { id, name, description } }) => (
    <Link to={`/application/${id}`}>
        <ListItem primaryText={name} secondaryText={description} />
    </Link>
);

export const ApplicationList = ({ applications, header }) => {
    if (!applications) return <p>No applications</p>;
    return (
        <List>
            {header}
            {applications.map(application => (
                <Application key={application.id} application={application} />
            ))}
        </List>
    );
};

const ApplicationListContainer = ({ isLoading, applications }) => {
    if (isLoading) return <LoadingIndicator />;
    return (
        <ApplicationList applications={applications} header={<h2>Applications</h2>} />
    );
};

const mapStateToProps = state => {
    const { applicationList, applicationListIsPending } = state;
    return {
        applications: applicationList.items,
        isLoading: applicationListIsPending,
    };
};
export default connect(mapStateToProps)(ApplicationListContainer);
