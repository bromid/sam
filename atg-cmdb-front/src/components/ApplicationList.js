import React from 'react';
import { Link } from 'react-router';
import { connect } from 'react-redux';
import * as Actions from '../actions/applicationActions';
import { List, ListItem } from 'material-ui/List';
import LoadingIndicator from './LoadingIndicator';

function Application({ application: { id, name, description } }) {
    return (
        <ListItem
            primaryText={
                <Link to={`/application/${id}`}>
                    {name}
                </Link>
            }
            secondaryText={description}
        />
    );
}

function ApplicationList({ applications }) {
    return (
        <List>
            <h2>Applications</h2>
            {applications.map(application => (
                <Application key={application.id} application={application} />
            ))}
        </List>
    );
}

const ApplicationListContainer = React.createClass({

    componentDidMount() {
        this.props.fetchApplicationList();
    },

    render() {
        const { isLoading, applications } = this.props;
        if (isLoading) return <LoadingIndicator />;
        if (!applications) return <p>No results</p>;
        return (
            <ApplicationList applications={applications} />
        );
    },
});

function mapStateToProps(state) {
    const { applicationList, applicationListIsLoading } = state;
    return {
        applications: applicationList.items,
        isLoading: applicationListIsLoading || applicationListIsLoading === null,
    };
}
export default connect(mapStateToProps, Actions)(ApplicationListContainer);
