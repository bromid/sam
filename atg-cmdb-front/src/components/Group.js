import React from 'react';
import { connect } from 'react-redux';
import size from 'lodash/size';
import isEmpty from 'lodash/isEmpty';
import * as groupActions from '../actions/groupActions';
import * as groupValidators from '../validators/groupValidators';
import LoadingIndicator from './LoadingIndicator';
import Attributes from './Attributes';
import ItemView from './ItemView';
import SubGroups from './SubGroups';
import { AssetList } from './AssetList';
import { ApplicationList } from './ApplicationList';
import { fromGroup, getAuthenticated } from '../reducers';

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
        updateName, updateDescription, onTagDelete, addSubGroup, removeSubGroup,
        isLoading, authenticated, patchIsPending, patchError,
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
            node: <SubGroups
                authenticated={authenticated}
                groups={groups}
                addGroup={addSubGroup}
                removeGroup={removeSubGroup}
            />,
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
            validateHeadline={groupValidators.name}
            description={description}
            updateDescription={updateDescription}
            validateDescription={groupValidators.description}
            tags={tags}
            onTagDelete={onTagDelete}
            meta={meta}
            tabs={tabs}
            isLoading={isLoading}
            patchIsPending={patchIsPending}
            patchError={patchError}
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

    addSubGroup(subGroupId) {
        const { group: { id }, addSubgroup } = this.props;
        addSubgroup(id, subGroupId);
    },

    removeSubGroup(subGroupId) {
        const { group: { id }, removeSubgroup } = this.props;
        removeSubgroup(id, subGroupId);
    },

    render() {
        const { group, isLoading, authenticated, patchIsPending, patchError } = this.props;

        if (isLoading && isEmpty(group)) return <LoadingIndicator />;

        return (
            <Group
                group={group}
                isLoading={isLoading}
                authenticated={authenticated}
                patchIsPending={patchIsPending}
                patchError={patchError}
                onTagDelete={this.onTagDelete}
                updateName={this.updateName}
                updateDescription={this.updateDescription}
                addSubGroup={this.addSubGroup}
                removeSubGroup={this.removeSubGroup}
            />
        );
    },
});

const mapStateToProps = (state) => ({
    group: fromGroup.getCurrent(state),
    fetchError: fromGroup.getCurrentError(state),
    patchResult: fromGroup.getPatchResult(state),
    patchError: fromGroup.getPatchResultError(state),
    patchIsPending: fromGroup.getPatchResultIsPending(state),
    isLoading: fromGroup.getCurrentIsPending(state),
    authenticated: getAuthenticated(state),
});

const Actions = {
    patchGroup: groupActions.patchGroup,
    addSubgroup: groupActions.addSubgroup,
    removeSubgroup: groupActions.removeSubgroup,
};
export default connect(mapStateToProps, Actions)(GroupContainer);
