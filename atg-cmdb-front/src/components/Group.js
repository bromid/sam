import React from 'react';
import { connect } from 'react-redux';
import size from 'lodash/size';
import isEmpty from 'lodash/isEmpty';
import * as groupActions from '../actions/groupActions';
import * as metaActions from '../actions/metaActions';
import LoadingIndicator from './LoadingIndicator';
import Attributes from './Attributes';
import ItemView from './ItemView';
import { GroupList } from './GroupList';
import { AssetList } from './AssetList';
import { ApplicationList } from './ApplicationList';

const patchNotification = (result, error, isPending) => {
    if (isPending) return {};
    if (!isEmpty(error)) {
        return {
            message: 'Failed to update group!',
            duration: 4000,
            action: {
                name: 'info',
            },
        };
    }
    if (!isEmpty(result)) {
        return {
            message: `Updated group ${result.name}`,
        };
    }
    return {};
};

const collectionSize = collection => {
    if (!collection) return ' (0)';
    return ` (${size(collection)})`;
};

const Group = props => {
    const {
        group: {
            name, description = '', applications, assets,
            tags, attributes, meta, groups,
        },
        notification, updateName, updateDescription,
        metaOpen, toggleMeta, onTagDelete,
    } = props;

    if (!name) return <p>No result</p>;
    const tabs = [
        {
            name: `Applications ${collectionSize(applications)}`,
            node: <ApplicationList applications={applications} />,
        },
        {
            name: `Assets ${collectionSize(assets)}`,
            node: <AssetList assets={assets} />,
        },
        {
            name: `Sub groups ${collectionSize(groups)}`,
            node: <GroupList groups={groups} />,
        },
        {
            name: `Attributes ${collectionSize(attributes)}`,
            node: <Attributes attributes={attributes} />,
        },
    ];
    return (
        <ItemView
            headline={name}
            updateHeadline={updateName}
            description={description}
            updateDescription={updateDescription}
            tags={tags}
            onTagDelete={onTagDelete}
            meta={meta}
            metaOpen={metaOpen}
            toggleMeta={toggleMeta}
            tabs={tabs}
            notification={notification}
        />
    );
};

const GroupContainer = React.createClass({

    getInitialState() {
        return { initiated: false };
    },

    componentDidMount() {
        const { id, fetchGroup } = this.props;
        fetchGroup(id);
    },

    componentWillReceiveProps(newProps) {
        const { id, patchResult, fetchGroup } = this.props;
        const { id: newId, patchResult: newPatchResult } = newProps;

        const isDifferentEtag = newPatchResult.etag !== patchResult.etag;
        const isUpdatedEtag = !isEmpty(newPatchResult) && isDifferentEtag;
        if (newId !== id || isUpdatedEtag) {
            this.setState({ initiated: true });
            fetchGroup(newId);
        }
    },

    onTagDelete(name) {
        return name;
    },

    updateName(name) {
        const { id, patchGroup, group: { meta } } = this.props;
        patchGroup(id, { name }, {
            hash: meta.hash,
        });
    },

    updateDescription(description) {
        const { id, patchGroup, group: { meta } } = this.props;
        patchGroup(id, { description }, {
            hash: meta.hash,
        });
    },

    render() {
        const {
            group, isLoading,
            metaOpen, toggleMeta,
            patchResult, patchError, patchIsPending,
        } = this.props;

        if (isLoading && !this.state.initiated) return <LoadingIndicator />;
        return (
            <Group
                group={group}
                isLoading={isLoading}
                onTagDelete={this.onTagDelete}
                metaOpen={metaOpen}
                toggleMeta={toggleMeta}
                updateName={this.updateName}
                updateDescription={this.updateDescription}
                notification={() => patchNotification(patchResult, patchError, patchIsPending)}
            />
        );
    },
});

function mapStateToProps(state, props) {
    const {
        metaOpen,
        group, groupError, groupIsPending,
        groupPatchResult, groupPatchResultError, groupPatchResultIsPending,
    } = state;
    const { id } = props.params;
    return {
        id,
        metaOpen,
        group,
        fetchError: groupError,
        patchResult: groupPatchResult,
        patchError: groupPatchResultError,
        patchIsPending: groupPatchResultIsPending,
        isLoading: groupIsPending || groupIsPending === null,
    };
}

const Actions = { ...groupActions, ...metaActions };
export default connect(mapStateToProps, Actions)(GroupContainer);
