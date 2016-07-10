import React from 'react';
import { Link } from 'react-router';
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
        const {
            isLoading,
            application: {
                name, description, group, attributes, meta,
            },
        } = this.props;
        if (isLoading) return <LoadingIndicator />;

        const tabs = [
            {
                name: 'Details',
                node: (
                    <div>
                        <dl>
                            <dt>Group</dt>
                            <dd>{group && <Link to={`/group/${group.id}`}>{group.name}</Link>}</dd>
                        </dl>
                        <Attributes attributes={attributes} />
                    </div>
                ),
            },
        ];
        return (
            <ItemView
                headline={name}
                description={description}
                meta={meta}
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
