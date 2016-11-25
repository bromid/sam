import React from 'react';
import { connect } from 'react-redux';
import { Link } from 'react-router';
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
            tags, attributes, meta, groups,
        },
        isLoading, isAuthenticated, patchIsPending, patchError,
        onUpdateName, onUpdateDescription, onTagDelete,
        onAddSubGroup, onRemoveSubGroup, onRefresh, onDelete,
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
                isAuthenticated={isAuthenticated}
                groups={groups}
                addGroup={onAddSubGroup}
                removeGroup={onRemoveSubGroup}
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
        const {
            group, isLoading, isAuthenticated, patchIsPending, patchError,
            fetchGroup, deleteGroup,
        } = this.props;

        if (isLoading && isEmpty(group)) return <LoadingIndicator />;

        return (
            <Group
                group={group}
                isLoading={isLoading}
                isAuthenticated={isAuthenticated}
                patchIsPending={patchIsPending}
                patchError={patchError}
                onTagDelete={this.onTagDelete}
                onUpdateName={this.updateName}
                onUpdateDescription={this.updateDescription}
                onAddSubGroup={this.addSubGroup}
                onRemoveSubGroup={this.removeSubGroup}
                onRefresh={() => fetchGroup(group.id)}
                onDelete={() => deleteGroup(group.id)}
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
    isAuthenticated: fromAuth.getIsAuthenticated(state),
});

const Actions = {
    patchGroup: groupActions.patchGroup,
    fetchGroup: groupActions.fetchGroup,
    deleteGroup: groupActions.deleteGroup,
    addSubgroup: groupActions.addSubgroup,
    removeSubgroup: groupActions.removeSubgroup,
};
export default connect(mapStateToProps, Actions)(GroupContainer);
