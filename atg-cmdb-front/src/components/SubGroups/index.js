import React, { PropTypes } from 'react';
import RaisedButton from 'material-ui/RaisedButton';
import AddSubGroup from './AddSubGroup';
import { flexWrapperStyle } from '../../style';
import { GroupList } from '../GroupList';
import * as groupValidators from '../../validators/groupValidators';

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
        <GroupList groups={groups} />
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
