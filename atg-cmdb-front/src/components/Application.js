import React from 'react';
import { connect } from 'react-redux';
import * as Actions from '../actions/applicationActions';
import LoadingIndicator from './LoadingIndicator';
import Attributes from './Attributes';
import ItemView from './ItemView';

const ApplicationContainer = React.createClass({

    componentDidMount() {
        const { id, fetchApplication } = this.props;
        fetchApplication(id);
    },

    render() {
        const { application, isLoading } = this.props;
        if (isLoading) return <LoadingIndicator />;

        const tabs = [
            {
                name: 'Attributes',
                node: <Attributes attributes={application.attributes} />,
            },
        ];
        return (
            <ItemView
                headline={application.name}
                description={application.description}
                meta={application.meta}
                tabs={tabs}
            />
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
