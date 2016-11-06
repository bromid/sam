import React, { PropTypes } from 'react';
import { connect } from 'react-redux';
import AutoComplete from 'material-ui/AutoComplete';
import SaveCancelForm from '../SaveCancelForm';
import * as groupActions from '../../actions/groupActions';
import { fromGroup } from '../../reducers';

const AddSubGroup = (props) => {
    const { value, errorText, groupIds, onChange, onCancel, onCreate, onSave, addRef } = props;
    return (
        <div>
            <h3>Add an existing group</h3>
            <SaveCancelForm columnStyle={true} cancel={onCancel} save={onSave}>
                <AutoComplete
                    id="group-id"
                    value={value}
                    dataSource={groupIds}
                    errorText={errorText}
                    floatingLabelText="Group id"
                    onUpdateInput={onChange}
                    onNewRequest={onChange}
                    ref={(ref) => addRef(ref)}
                    fullWidth={true}
                />
            </SaveCancelForm>
            or <a href="#" onTouchTap={onCreate}>create a new group</a>
        </div>
    );
};

const AddSubGroupContainer = React.createClass({
    propTypes: {
        value: PropTypes.string,
        errorText: PropTypes.string,
        groupIds: PropTypes.array,
        onChange: PropTypes.func.isRequired,
        onCancel: PropTypes.func.isRequired,
        onSave: PropTypes.func.isRequired,
        onCreate: PropTypes.func.isRequired,

    },

    componentWillMount() {
        this.props.fetchGroupIds();
    },

    onSave(event) {
        event.preventDefault();
        const { value, errorText, onSave } = this.props;
        if (errorText.length > 1) {
            this.fieldRef.focus();
        } else {
            onSave(value);
        }
    },

    saveRefAndFocus(ref) {
        if (ref) {
            this.fieldRef = ref;
            ref.focus();
        }
    },

    render() {
        const { value, errorText, groupIds = [], onChange, onCancel, onCreate } = this.props;
        return (
            <AddSubGroup
                value={value}
                errorText={errorText}
                groupIds={groupIds}
                onChange={onChange}
                onCancel={onCancel}
                onCreate={onCreate}
                onSave={this.onSave}
                addRef={this.saveRefAndFocus}
            />
        );
    },
});

const mapStateToProps = (state) => ({
    groupIds: fromGroup.getIds(state),
});

const Actions = {
    fetchGroupIds: groupActions.fetchGroupIds,
};
export default connect(mapStateToProps, Actions)(AddSubGroupContainer);
