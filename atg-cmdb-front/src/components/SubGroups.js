import React from 'react';
import RaisedButton from 'material-ui/RaisedButton';
import { flexWrapperStyle } from '../style';
import { GroupList } from './GroupList';
import PersistableField from './PersistableField';

const AddSubGroup = ({ groupId, change, cancel }) => (
    <div>
        <h3>Add sub group</h3>
        <PersistableField
            id="group-id"
            value={groupId}
            floatingLabelText="Group id"
            change={change}
            cancel={cancel}
            columnStyle={true}
        />
    </div>
);

const SubGroupsList = ({ authenticated, groups, addGroup }) => (
    <div>
        <div style={{ ...flexWrapperStyle, alignItems: 'center' }}>
            <div style={{ flex: 1 }}>
                <h3>Sub groups</h3>
            </div>
            {authenticated &&
                <RaisedButton
                    label="Add group"
                    onTouchTap={addGroup}
                    style={{ borderRadius: 3 }}
                />
            }
        </div>
        <GroupList groups={groups} />
    </div>
);

const SubGroups = React.createClass({

    getInitialState() {
        return {
            addGroup: false,
            groupId: '',
        };
    },

    addGroup(addGroup = true) {
        this.setState({ addGroup });
    },

    cancelAddGroup() {
        this.setState({ groupId: '' });
        this.addGroup(false);
    },

    changeGroupId(event) {
        const groupId = event.target.value;
        this.setState({ groupId });
    },

    render() {
        const { authenticated, groups } = this.props;
        return (
            (this.state.addGroup) ?
                <AddSubGroup
                    groupId={this.state.groupId}
                    change={this.changeGroupId}
                    cancel={this.cancelAddGroup}
                /> :
                <SubGroupsList
                    groups={groups}
                    authenticated={authenticated}
                    addGroup={this.addGroup}
                />
        );
    },
});
export default SubGroups;
