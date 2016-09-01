import React, { PropTypes } from 'react';
import { browserHistory } from 'react-router';
import isEmpty from 'lodash/isEmpty';
import IconButton from 'material-ui/IconButton';
import IconMenu from 'material-ui/IconMenu';
import MenuItem from 'material-ui/MenuItem';
import { ListItem } from 'material-ui/List';
import VertIcon from 'material-ui/svg-icons/navigation/more-vert';
import { GroupText } from '../GroupList';

const openGroup = (groupId) => {
    browserHistory.push(`/group/${groupId}`);
};

const GroupListItem = React.createClass({
    propTypes: {
        group: PropTypes.object.isRequired,
        nestedItems: PropTypes.array,
        nestedLevel: PropTypes.number,
        remove: PropTypes.func,
    },

    getInitialState() {
        return { nestedOpen: false };
    },

    toggleNested() {
        this.setState({ nestedOpen: !this.state.nestedOpen });
    },

    render() {
        const { group, nestedItems, nestedLevel, remove } = this.props;
        const { nestedOpen } = this.state;

        const hasNestedItems = !isEmpty(nestedItems);
        const toggleNestedText = (nestedOpen) ? 'Collapse' : 'Expand';

        const menuButton = (
            <IconButton tooltip="menu">
                <VertIcon />
            </IconButton>
        );

        const menu = (nestedLevel === 0) ? (
            <IconMenu iconButtonElement={menuButton} touchTapCloseDelay={100}>
                <MenuItem onTouchTap={() => openGroup(group.id)}>Open</MenuItem>
                {remove &&
                    <MenuItem onTouchTap={() => remove(group.id)}>Remove</MenuItem>
                }
                {hasNestedItems &&
                    <MenuItem onTouchTap={this.toggleNested}>{toggleNestedText}</MenuItem>
                }
            </IconMenu>
        ) : undefined;

        return (
            <ListItem
                primaryText={<GroupText group={group} />}
                secondaryText={group.description}
                primaryTogglesNestedList={true}
                open={nestedOpen}
                disabled={!hasNestedItems}
                nestedItems={nestedItems}
                nestedLevel={nestedLevel}
                rightIconButton={menu}
                onNestedListToggle={this.toggleNested}
            />
        );
    },
});
export default GroupListItem;
