import React from 'react';
import { connect } from 'react-redux';
import * as Actions from '../actions/applicationActions';
import { List, ListItem } from 'material-ui/List';
import Subheader from 'material-ui/Subheader';
import Attributes from './Attributes';
import LoadingIndicator from "./LoadingIndicator";

function Application({ application: { name, group, attributes } }) {
    const content = (
        <span>
            {group.id}
            {attributes && <Attributes attributes={attributes} />}
        </span>
    );

    return (
        <ListItem
            primaryText={name}
            secondaryText={content}
        />
    );
}

function Applications({ applications }) {
    return (
        <List>
            <h2 style={{margin: 15}}>Applications</h2>
            {applications.map(application => (
                <Application key={application.id} application={application} />
            ))}
        </List>
    );
}

const ApplicationsContainer = React.createClass({

    componentDidMount() {
        this.props.fetchApplications();
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
    const { applications, applicationsIsLoading } = state;
    return {
        applications: applications.items,
        isLoading: applicationsIsLoading || applicationsIsLoading === null,
    };
}
export default connect(mapStateToProps, Actions)(ApplicationsContainer);