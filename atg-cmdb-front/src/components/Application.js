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

const patchNotification = (result, error, isPending) => {
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
};

const ApplicationContainer = React.createClass({

    updateDescription(description) {
        const { patchApplication, application: { id, meta } } = this.props;
        patchApplication(id, { description }, { hash: meta.hash });
    },

    updateName(name) {
        const { patchApplication, application: { id, meta } } = this.props;
        patchApplication(id, { name }, { hash: meta.hash });
    },

    render() {
        const {
            application, isLoading,
            metaOpen, toggleMeta,
            patchResult, patchError, patchIsPending,
        } = this.props;

        if (isLoading && isEmpty(application)) return <LoadingIndicator />;

        const { id, name, description = '', group, attributes, meta } = application;

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
                isLoading={isLoading}
            />
        );
    },
});

const mapStateToProps = (state) => {
    const {
        metaOpen,
        application, applicationError, applicationIsPending,
        applicationPatchResult, applicationPatchResultError, applicationPatchResultIsPending,
    } = state;
    return {
        metaOpen,
        application,
        fetchError: applicationError,
        patchResult: applicationPatchResult,
        patchError: applicationPatchResultError,
        patchIsPending: applicationPatchResultIsPending,
        isLoading: applicationIsPending,
    };
};

const Actions = {
    patchApplication: applicationActions.patchApplication,
    toggleMeta: metaActions.toggleMeta,
};
export default connect(mapStateToProps, Actions)(ApplicationContainer);
