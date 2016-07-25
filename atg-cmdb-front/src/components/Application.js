import React from 'react';
import { Link } from 'react-router';
import { connect } from 'react-redux';
import isEmpty from 'lodash/isEmpty';
import * as applicationActions from '../actions/applicationActions';
import * as metaActions from '../actions/metaActions';
import LoadingIndicator from './LoadingIndicator';
import Attributes from './Attributes';
import ItemView from './ItemView';
import ApplicationDeployments from './ApplicationDeployments';

const isLoadingNew = (id, application, loading) =>
  loading && (isEmpty(application) || id !== application.id);

function patchNotification(result, error, isPending) {
    if (isPending) return {};
    if (!isEmpty(error)) {
        return {
            message: 'Failed to update application!',
            duration: 4000,
            action: {
                name: 'info',
            },
        };
    }
    if (!isEmpty(result)) {
        return {
            message: `Updated application ${result.name}`,
        };
    }
    return {};
}

const ApplicationContainer = React.createClass({

    updateDescription(description) {
        const { id, patchApplication, application: { meta } } = this.props;
        patchApplication(id, { description }, {
            hash: meta.hash,
        });
    },

    updateName(name) {
        const { id, patchApplication, application: { meta } } = this.props;
        patchApplication(id, { name }, {
            hash: meta.hash,
        });
    },

    render() {
        const {
            id, application, isLoading,
            metaOpen, toggleMeta,
            patchResult, patchError, patchIsPending,
        } = this.props;

        if (isLoadingNew(id, application, isLoading)) return <LoadingIndicator />;

        const { name, description = '', group, attributes, meta } = application;

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
                tabs={tabs}
                headline={name}
                updateHeadline={this.updateName}
                description={description}
                updateDescription={this.updateDescription}
                meta={meta}
                metaOpen={metaOpen}
                toggleMeta={toggleMeta}
                notification={() => patchNotification(patchResult, patchError, patchIsPending)}
            />
        );
    },
});

function mapStateToProps(state, props) {
    const {
        metaOpen,
        application, applicationError, applicationIsPending,
        applicationPatchResult, applicationPatchResultError, applicationPatchResultIsPending,
    } = state;
    const { id } = props.params;
    return {
        id,
        metaOpen,
        application,
        fetchError: applicationError,
        patchResult: applicationPatchResult,
        patchError: applicationPatchResultError,
        patchIsPending: applicationPatchResultIsPending,
        isLoading: applicationIsPending,
    };
}

const Actions = {
    patchApplication: applicationActions.patchApplication,
    toggleMeta: metaActions.toggleMeta,
};
export default connect(mapStateToProps, Actions)(ApplicationContainer);
