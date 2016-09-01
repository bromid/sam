import React from 'react';
import { Link } from 'react-router';
import { connect } from 'react-redux';
import isEmpty from 'lodash/isEmpty';
import * as applicationValidators from '../validators/applicationValidators';
import * as applicationActions from '../actions/applicationActions';
import LoadingIndicator from '../components/LoadingIndicator';
import Attributes from '../components/Attributes';
import ItemView from '../components/ItemView';
import ApplicationDeployments from '../components/ApplicationDeployment';
import { fromApplication } from '../reducers';

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
        const { application, isLoading, patchIsPending, patchError } = this.props;

        if (isLoading && isEmpty(application)) return <LoadingIndicator />;

        const { id, name, description = '', group, attributes, meta } = application;

        const tabs = [
            {
                name: 'Details',
                node: (
                    <div>
                        {group &&
                            <dl>
                                <dt>Group</dt>
                                <dd><Link to={`/group/${group.id}`}>{group.name}</Link></dd>
                            </dl>
                        }
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
                validateHeadline={applicationValidators.name}
                description={description}
                updateDescription={this.updateDescription}
                validateDescription={applicationValidators.description}
                meta={meta}
                isLoading={isLoading}
                patchIsPending={patchIsPending}
                patchError={patchError}
            />
        );
    },
});

const mapStateToProps = (state) => ({
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
