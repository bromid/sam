import React from 'react';
import { Link } from 'react-router';
import { connect } from 'react-redux';
import { List, ListItem } from 'material-ui/List';
import RaisedButton from 'material-ui/RaisedButton';
import { flexWrapperStyle } from '../style';
import LoadingIndicator from './LoadingIndicator';
import { fromApplication, getAuthenticated } from '../reducers';

export const ApplicationLink = ({ application: { id, name, description } }) => (
    <Link to={`/application/${id}`}>
        <ListItem primaryText={name} secondaryText={description} />
    </Link>
);

export const ApplicationList = ({ applications }) => {
    if (!applications) return <p>No applications</p>;
    return (
        <List>
            {applications.map((application) => (
                <ApplicationLink key={application.id} application={application} />
            ))}
        </List>
    );
};

const Applications = ({ applications, authenticated }) => (
    <div>
        <div style={{ ...flexWrapperStyle, alignItems: 'center' }}>
            <div style={{ flex: 1 }}>
                <h2>Applications</h2>
            </div>
            {authenticated &&
                <Link to="/application/new">
                    <RaisedButton
                        label="Add application"
                        style={{ borderRadius: 3 }}
                    />
                </Link>
            }
        </div>
        <ApplicationList applications={applications} />
    </div>
);

const ApplicationsContainer = ({ isLoading, authenticated, applications }) => {
    if (isLoading) return <LoadingIndicator />;
    return (
        <Applications
            applications={applications}
            authenticated={authenticated}
        />
    );
};

const mapStateToProps = (state) => ({
    applications: fromApplication.getList(state),
    isLoading: fromApplication.getListIsPending(state),
    authenticated: getAuthenticated(state),
});
export default connect(mapStateToProps)(ApplicationsContainer);
