import React from 'react';
import { Link } from 'react-router';
import { connect } from 'react-redux';
import isEmpty from 'lodash/isEmpty';
import * as applicationActions from '../actions/applicationActions';
import LoadingIndicator from './LoadingIndicator';
import Attributes from './Attributes';
import ItemView from './ItemView';
import ApplicationDeployments from './ApplicationDeployments';
import { fromApplication, getIsMetaOpen } from '../reducers';

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
        const { application, isLoading } = this.props;

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
                isLoading={isLoading}
            />
        );
    },
});

const mapStateToProps = (state) => ({
    metaOpen: getIsMetaOpen(state),
    application: fromApplication.getCurrent(state),
    fetchError: fromApplication.getCurrentError(state),
    patchResult: fromApplication.getPatchResult(state),
    patchError: fromApplication.getPatchResultError(state),
    patchIsPending: fromApplication.getPatchResultIsPending(state),
    isLoading: fromApplication.getCurrentIsPending(state),
});

const Actions = {
    patchApplication: applicationActions.patchApplication,
};
export default connect(mapStateToProps, Actions)(ApplicationContainer);
