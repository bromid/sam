import React, { PropTypes } from 'react';
import isEmpty from 'lodash/isEmpty';
import IconButton from 'material-ui/IconButton';
import IconMenu from 'material-ui/IconMenu';
import MenuItem from 'material-ui/MenuItem';
import RaisedButton from 'material-ui/RaisedButton';
import { ListItem } from 'material-ui/List';
import VertIcon from 'material-ui/svg-icons/navigation/more-vert';
import AddSubGroup from './AddSubGroup';
import { flexWrapperStyle } from '../../style';
import { GroupList, GroupText } from '../GroupList';
import * as groupValidators from '../../validators/groupValidators';

const menuButton = (
    <IconButton tooltip="menu">
        <VertIcon />
    </IconButton>
);

const menu = (group) => (
    <IconMenu iconButtonElement={menuButton}>
        <MenuItem onTouchTap={() => console.info(`Expand ${group.id}`)}>Expand</MenuItem>
        <MenuItem onTouchTap={() => console.info(`Removed ${group.id}`)}>Remove</MenuItem>
    </IconMenu>
);

const GroupListItem = ({ group, nestedItems, nestedLevel }) => (
    <ListItem
        primaryText={<GroupText group={group} />}
        secondaryText={group.description}
        primaryTogglesNestedList={true}
        disabled={isEmpty(nestedItems)}
        nestedItems={nestedItems}
        nestedLevel={nestedLevel}
        rightIconButton={menu(group)}
        onNestedListToggle={() => console.info('Toggle Nested')}
    />
);

const SubGroupsList = ({ authenticated, groups, onAddGroup }) => (
    <div>
        <div style={{ ...flexWrapperStyle, alignItems: 'center' }}>
            <div style={{ flex: 1 }}>
                <h3>Sub groups</h3>
            </div>
            {authenticated &&
                <RaisedButton
                    label="Add group"
                    onTouchTap={onAddGroup}
                    style={{ borderRadius: 3 }}
                />
            }
        </div>
        <GroupList groups={groups} listItem={GroupListItem} />
    </div>
);

const SubGroups = React.createClass({
    propTypes: {
        groups: PropTypes.array,
        addGroup: PropTypes.func,
        authenticated: PropTypes.object,
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
        const { authenticated, groups } = this.props;
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
                    authenticated={authenticated}
                    onAddGroup={this.startAddGroup}
                />
        );
    },
});
export default SubGroups;
