import React, { PropTypes } from 'react';
import RaisedButton from 'material-ui/RaisedButton';
import AddSubGroup from './AddSubGroup';
import GroupListItem from './GroupListItem';
import GroupList from '../GroupList';
import { flexWrapperStyle } from '../../style';
import * as groupValidators from '../../validators/groupValidators';

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
        addGroup: PropTypes.func,
        removeGroup: PropTypes.func,
        isAuthenticated: PropTypes.bool,
    },

    getInitialState() {
        return { groupId: '', groupIdErrorText: '', isAddingGroup: false };
    },

    onAddGroup(groupId) {
        const errorText = this.changeGroupId(groupId);
        if (errorText.length < 1) {
            this.cancelAddGroup();
            this.props.addGroup(groupId);
        }
    },

    cancelAddGroup() {
        this.setState({ groupId: '', groupIdErrorText: '', isAddingGroup: false });
    },

    startAddGroup() {
        this.setState({ isAddingGroup: true });
    },

    changeGroupId(value) {
        const groupId = value.trim();
        const groupIdErrorText = groupValidators.id(groupId);
        this.setState({ groupId, groupIdErrorText });
        return groupIdErrorText;
    },

    render() {
        const { isAuthenticated, groups, removeGroup } = this.props;
        const { isAddingGroup, groupId, groupIdErrorText } = this.state;
        return (
            (isAddingGroup) ?
                <AddSubGroup
                    value={groupId}
                    errorText={groupIdErrorText}
                    change={this.changeGroupId}
                    cancel={this.cancelAddGroup}
                    save={this.onAddGroup}
                /> :
                <SubGroupsList
                    groups={groups}
                    isAuthenticated={isAuthenticated}
                    onAddGroup={this.startAddGroup}
                    onRemoveGroup={removeGroup}
                />
        );
    },
});
export default SubGroups;
