import React from 'react';
import AutoComplete from 'material-ui/AutoComplete';
import RaisedButton from 'material-ui/RaisedButton';
import { flexWrapperStyle } from '../style';
import { GroupList } from './GroupList';
import SaveCancelForm from './SaveCancelForm';

const AddSubGroup = ({ groupId, change, cancel }) => (
    <div>
        <h3>Add sub group</h3>
        <SaveCancelForm columnStyle={true} cancel={cancel} save={() => console.info('Save')}>
            <AutoComplete
                id="group-id"
                value={groupId}
                dataSource={[]}
                floatingLabelText="Group id"
                onChange={change}
                fullWidth={true}
            />
        </SaveCancelForm>
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
