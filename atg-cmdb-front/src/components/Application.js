import React from 'react';
import { connect } from 'react-redux';
import * as Actions from '../actions/applicationActions';
import LoadingIndicator from './LoadingIndicator';
import Attributes from './Attributes';

const ApplicationContainer = React.createClass({

    componentDidMount() {
        const { id, fetchApplication } = this.props;
        fetchApplication(id);
    },

    render() {
        const { application, isLoading } = this.props;
        if (isLoading) return <LoadingIndicator />;
        return (
            <div style={{ padding: '8px 0' }}>
                <h2>{application.name}</h2>
                <div style={{ margin: 16 }}>
                    <p>{application.description}</p>
                    <Attributes attributes={application.attributes} />
                </div>
            </div>
        );
    },
});

function mapStateToProps(state, props) {
    const { application, applicationIsLoading } = state;
    const { id } = props.params;
    return {
        id,
        application,
        isLoading: applicationIsLoading || applicationIsLoading === null,
    };
}
export default connect(mapStateToProps, Actions)(ApplicationContainer);
