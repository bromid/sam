import React from 'react';
import { connect } from 'react-redux';
import * as Actions from '../actions/applicationActions';
import { List, ListItem } from 'material-ui/List';
import Subheader from 'material-ui/Subheader';
import LinearProgress from 'material-ui/LinearProgress';
import Attributes from './Attributes';

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
            <Subheader>Applications</Subheader>
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
        if (isLoading) return <LinearProgress />;

        return (
            <Applications applications={applications} isLoading={isLoading} />
        );
    },
});

function mapStateToProps(state) {
    const { applications, applicationsIsLoading } = state;
    return {
        applications: applications.items,
        isLoading: applicationsIsLoading || !applications.items,
    };
}
export default connect(mapStateToProps, Actions)(ApplicationsContainer);