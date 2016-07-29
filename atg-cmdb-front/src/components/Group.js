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
import * as fromGroup from '../reducers/group';

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

const collectionSize = (collection) => {
    if (!collection) return ' (0)';
    return ` (${size(collection)})`;
};

const Group = (props) => {
    const {
        group: {
            name, description = '', applications, assets,
            tags, attributes, meta, groups,
        },
        notification, updateName, updateDescription,
        metaOpen, toggleMeta, onTagDelete, isLoading,
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
            isLoading={isLoading}
        />
    );
};

const GroupContainer = React.createClass({

    onTagDelete(name) {
        return name;
    },

    updateName(name) {
        const { patchGroup, group: { id, meta } } = this.props;
        patchGroup(id, { name }, { hash: meta.hash });
    },

    updateDescription(description) {
        const { patchGroup, group: { id, meta } } = this.props;
        patchGroup(id, { description }, { hash: meta.hash });
    },

    render() {
        const {
            group, isLoading,
            metaOpen, toggleMeta,
            patchResult, patchError, patchIsPending,
        } = this.props;

        if (isLoading && isEmpty(group)) return <LoadingIndicator />;

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

const mapStateToProps = (state) => {
    const {
        metaOpen,
        group,
    } = state;
    return {
        metaOpen,
        group: fromGroup.getCurrent(group),
        fetchError: fromGroup.getCurrentError(group),
        patchResult: fromGroup.getPatchResult(group),
        patchError: fromGroup.getPatchResultError(group),
        patchIsPending: fromGroup.getPatchResultIsPending(group),
        isLoading: fromGroup.getCurrentIsPending(group),
    };
};

const Actions = {
    patchGroup: groupActions.patchGroup,
    toggleMeta: metaActions.toggleMeta,
};
export default connect(mapStateToProps, Actions)(GroupContainer);
