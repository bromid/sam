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

const ApplicationContainer = React.createClass({

    componentDidMount() {
        const { id, fetchApplication } = this.props;
        fetchApplication(id);
    },

    componentWillReceiveProps(newProps) {
        const { id, patchResult, fetchApplication } = this.props;
        const { id: newId, patchResult: newPatchResult } = newProps;

        const isDifferentEtag = newPatchResult.etag !== patchResult.etag;
        const isUpdatedEtag = !isEmpty(newPatchResult) && isDifferentEtag;
        if (newId !== id || isUpdatedEtag) {
            fetchApplication(newId);
        }
    },

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
                tabs={tabs}
                headline={name}
                updateHeadline={this.updateName}
                description={description}
                updateDescription={this.updateDescription}
                meta={meta}
                metaOpen={metaOpen}
                toggleMeta={toggleMeta}
            />
        );
    },
});

function mapStateToProps(state, props) {
    const { metaOpen, application, applicationPatchResult, applicationIsPending } = state;
    const { id } = props.params;
    return {
        id,
        metaOpen,
        application,
        patchResult: applicationPatchResult,
        isLoading: applicationIsPending || applicationIsPending === null,
    };
}

const Actions = { ...applicationActions, ...metaActions };
export default connect(mapStateToProps, Actions)(ApplicationContainer);
