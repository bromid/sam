import React from 'react';
import { connect } from 'react-redux';
import { Link, browserHistory } from 'react-router';
import isEmpty from 'lodash/isEmpty';
import { collectionSize } from '../helpers';
import * as groupActions from '../actions/groupActions';
import * as groupValidators from '../validators/groupValidators';
import LoadingIndicator from '../components/LoadingIndicator';
import Attributes from '../components/Attributes';
import ItemView from '../components/ItemView';
import SubGroups from '../components/SubGroups';
import { AssetList } from '../components/AssetList';
import { ApplicationList } from '../components/ApplicationList';
import RefreshButton from '../components/ItemView/RefreshButton';
import DeleteButton from '../components/ItemView/DeleteButton';
import DashboardButton from '../components/ItemView/DashboardButton';
import { fromGroup, fromAuth } from '../reducers';

const Group = (props) => {
    const {
        group: {
            id, name, description = '', applications, assets,
            tags, attributes, meta, groups = [],
        },
        isLoading, isAuthenticated, createIsPending, createError, patchIsPending, patchError,
        onUpdateName, onUpdateDescription, onTagDelete,
        onAddSubGroup, onRemoveSubGroup, onCreateSubGroup, onRefresh, onDelete,
    } = props;

    if (!id) return <p>No group found</p>;
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
                isAuthenticated={isAuthenticated}
                groups={groups}
                onAddGroup={onAddSubGroup}
                onRemoveGroup={onRemoveSubGroup}
                onCreateGroup={onCreateSubGroup}
                createIsPending={createIsPending}
                createError={createError}
            />,
        },
        {
            name: `Attributes ${collectionSize(attributes)}`,
            node: <Attributes attributes={attributes} />,
        },
    ];

    const buttons = [<RefreshButton key="refresh" onClick={onRefresh} />];
    if (isAuthenticated) {
        buttons.push(<DeleteButton key="delete" onClick={onDelete} />);
    }
    buttons.push(
        <Link key="dashboard" to={`/group/${id}/deployments`}>
            <DashboardButton tooltip="Deployments dashboard" />
        </Link>
    );
    return (
        <ItemView
            headline={name}
            updateHeadline={onUpdateName}
            validateHeadline={groupValidators.name}
            description={description}
            updateDescription={onUpdateDescription}
            validateDescription={groupValidators.description}
            tags={tags}
            onTagDelete={onTagDelete}
            meta={meta}
            tabs={tabs}
            isLoading={isLoading}
            patchIsPending={patchIsPending}
            patchError={patchError}
            buttons={buttons}
        />
    );
};

const GroupContainer = React.createClass({

    deleteTag(name) {
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

    deleteGroup() {
        const { group, deleteGroup } = this.props;
        deleteGroup(group.id, () => browserHistory.push('/group'));
    },

    refreshGroup() {
        const { group, fetchGroup } = this.props;
        fetchGroup(group.id);
    },

    addSubGroup(subGroupId) {
        const { group: { id }, addSubgroup } = this.props;
        addSubgroup(id, subGroupId);
    },

    removeSubGroup(subGroupId) {
        const { group: { id }, removeSubgroup } = this.props;
        removeSubgroup(id, subGroupId);
    },

    createSubGroup(group, callback) {
        this.props.createGroup(group, callback);
    },

    render() {
        const {
            group, isLoading, isAuthenticated,
            createIsPending, createError, patchIsPending, patchError,
        } = this.props;

        if (isLoading && isEmpty(group)) return <LoadingIndicator />;

        return (
            <Group
                group={group}
                isLoading={isLoading}
                isAuthenticated={isAuthenticated}
                createIsPending={createIsPending}
                createError={createError}
                patchIsPending={patchIsPending}
                patchError={patchError}
                onTagDelete={this.deleteTag}
                onUpdateName={this.updateName}
                onUpdateDescription={this.updateDescription}
                onAddSubGroup={this.addSubGroup}
                onRemoveSubGroup={this.removeSubGroup}
                onCreateSubGroup={this.createSubGroup}
                onRefresh={this.refreshGroup}
                onDelete={this.deleteGroup}
            />
        );
    },
});

const mapStateToProps = (state) => ({
    group: fromGroup.getCurrent(state),
    fetchError: fromGroup.getCurrentError(state),
    createIsPending: fromGroup.getCreateResultIsPending(state),
    createError: fromGroup.getCreateResultError(state),
    patchResult: fromGroup.getPatchResult(state),
    patchError: fromGroup.getPatchResultError(state),
    patchIsPending: fromGroup.getPatchResultIsPending(state),
    isLoading: fromGroup.getCurrentIsPending(state),
    isAuthenticated: fromAuth.getIsAuthenticated(state),
});

const Actions = {
    patchGroup: groupActions.patchGroup,
    fetchGroup: groupActions.fetchGroup,
    deleteGroup: groupActions.deleteGroup,
    createGroup: groupActions.createGroup,
    addSubgroup: groupActions.addSubgroup,
    removeSubgroup: groupActions.removeSubgroup,
};
export default connect(mapStateToProps, Actions)(GroupContainer);
