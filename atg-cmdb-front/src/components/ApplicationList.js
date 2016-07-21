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

function Applications({ applications }) {
    return (
        <List>
            <h2>Applications</h2>
            {applications.map(application => (
                <Application key={application.id} application={application} />
            ))}
        </List>
    );
}

const ApplicationsContainer = React.createClass({

    componentDidMount() {
        this.props.fetchApplicationList();
    },

    render() {
        const { isLoading, applications } = this.props;
        if (isLoading) return <LoadingIndicator />;
        if (!applications) return <p>No results</p>;
        return (
            <Applications applications={applications} />
        );
    },
});

function mapStateToProps(state) {
    const { applicationList, applicationListIsPending } = state;
    return {
        applications: applicationList.items,
        isLoading: applicationListIsPending || applicationListIsPending === null,
    };
}
export default connect(mapStateToProps, Actions)(ApplicationsContainer);
