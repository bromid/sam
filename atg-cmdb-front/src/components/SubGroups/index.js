import React, { PropTypes } from 'react';
import RaisedButton from 'material-ui/RaisedButton';
import AddSubGroup from './AddSubGroup';
import CreateSubGroup from './CreateSubGroup';
import GroupListItem from './GroupListItem';
import GroupList from '../GroupList';
import { flexWrapperStyle } from '../../style';
import * as groupValidators from '../../validators/groupValidators';

const State = {
    list: 'list',
    addSubGroup: 'addSubGroup',
    createSubGroup: 'createSubGroup',
};

const SubGroupsList = ({ isAuthenticated, groups, onAddGroup, onRemoveGroup }) => (
    <div>
        <div style={{ ...flexWrapperStyle, alignItems: 'center' }}>
            <div style={{ flex: 1 }}>
                <h3>Sub groups</h3>
            </div>
            {isAuthenticated &&
                <RaisedButton
                    label="Add group"
                    onTouchTap={onAddGroup}
                    style={{ borderRadius: 3 }}
                />
            }
        </div>
        <GroupList
            groups={groups}
            remove={(isAuthenticated) ? onRemoveGroup : undefined}
            listItem={GroupListItem}
        />
    </div>
);

const SubGroups = React.createClass({
    propTypes: {
        groups: PropTypes.array,
        onAddGroup: PropTypes.func.isRequired,
        onRemoveGroup: PropTypes.func.isRequired,
        onCreateGroup: PropTypes.func.isRequired,
        isAuthenticated: PropTypes.bool,
        createError: PropTypes.object,
        createIsPending: PropTypes.bool,
    },

    getInitialState() {
        return { groupId: '', groupIdErrorText: '', state: State.list };
    },

    onGroupCreated(groupId) {
        this.setState({ state: State.addSubGroup });
        this.handleChangeGroupId(groupId);
    },

    handleAddGroup(groupId) {
        const errorText = this.handleChangeGroupId(groupId);
        if (errorText.length < 1) {
            this.handleCancelAddOrCreateGroup();
            this.props.onAddGroup(groupId);
        }
    },

    handleCancelAddOrCreateGroup() {
        this.setState({ groupId: '', groupIdErrorText: '', state: State.list });
    },

    startAddGroup() {
        this.setState({ state: State.addSubGroup });
    },

    handleChangeGroupId(value) {
        const groupId = value.trim();
        const groupIdErrorText = groupValidators.id(groupId);
        this.setState({ groupId, groupIdErrorText });
        return groupIdErrorText;
    },

    handleStartCreateGroup() {
        this.setState({ state: State.createSubGroup });
    },

    handleCreateGroup(group) {
        this.props.onCreateGroup(group, () => this.onGroupCreated(group.id));
    },

    render() {
        const { isAuthenticated, groups, onRemoveGroup, createIsPending, createError } = this.props;
        const { state, groupId, groupIdErrorText } = this.state;
        switch (state) {
            case State.addSubGroup:
                return (
                    <AddSubGroup
                        value={groupId}
                        errorText={groupIdErrorText}
                        onChange={this.handleChangeGroupId}
                        onCancel={this.handleCancelAddOrCreateGroup}
                        onSave={this.handleAddGroup}
                        onCreate={this.handleStartCreateGroup}
                    />
                );
            case State.createSubGroup:
                return (
                    <CreateSubGroup
                        onCreate={this.handleCreateGroup}
                        onCancel={this.handleCancelAddOrCreateGroup}
                        createIsPending={createIsPending}
                        createError={createError}
                    />
                );
            default:
                return (
                    <SubGroupsList
                        groups={groups}
                        isAuthenticated={isAuthenticated}
                        onAddGroup={this.startAddGroup}
                        onRemoveGroup={onRemoveGroup}
                    />
                );
        }
    },
});
export default SubGroups;
