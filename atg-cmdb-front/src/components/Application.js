import React from 'react';
import { Link } from 'react-router';
import { connect } from 'react-redux';
import * as applicationActions from '../actions/applicationActions';
import * as metaActions from '../actions/metaActions';
import LoadingIndicator from './LoadingIndicator';
import Attributes from './Attributes';
import ItemView from './ItemView';
import ApplicationDeployments from './ApplicationDeployments';

const ApplicationContainer = React.createClass({

    componentDidMount() {
        const { id, fetchApplication } = this.props;
        fetchApplication(id);
    },

    componentWillReceiveProps(newProps) {
        const { id, fetchApplication } = this.props;
        const { id: newId } = newProps;

        if (newId !== id) {
            fetchApplication(newId);
        }
    },

    render() {
        const {
            isLoading,
            metaOpen,
            toggleMeta,
            application: {
                id, name, description, group, attributes, meta,
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
            {
                name: 'Deployments',
                node: <ApplicationDeployments id={id} />,
            },
        ];
        return (
            <ItemView
                headline={name}
                description={description}
                meta={meta}
                metaOpen={metaOpen}
                toggleMeta={toggleMeta}
                tabs={tabs}
            />
        );
    },
});

function mapStateToProps(state, props) {
    const { metaOpen, application, applicationIsLoading } = state;
    const { id } = props.params;
    return {
        id,
        metaOpen,
        application,
        isLoading: applicationIsLoading || applicationIsLoading === null,
    };
}

const Actions = { ...applicationActions, ...metaActions };
export default connect(mapStateToProps, Actions)(ApplicationContainer);
