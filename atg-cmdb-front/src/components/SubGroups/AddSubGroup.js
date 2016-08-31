import React, { PropTypes } from 'react';
import { connect } from 'react-redux';
import AutoComplete from 'material-ui/AutoComplete';
import SaveCancelForm from '../SaveCancelForm';
import * as groupActions from '../../actions/groupActions';
import { fromGroup } from '../../reducers';

const AddSubGroup = ({ value, errorText, groupIds = [], change, cancel, save, addRef }) => (
    <div>
        <h3>Add sub group</h3>
        <SaveCancelForm columnStyle={true} cancel={cancel} save={save}>
            <AutoComplete
                id="group-id"
                value={value}
                dataSource={groupIds}
                errorText={errorText}
                floatingLabelText="Group id"
                onUpdateInput={change}
                onNewRequest={change}
                ref={(ref) => addRef(ref)}
                fullWidth={true}
            />
        </SaveCancelForm>
    </div>
);

const AddSubGroupContainer = React.createClass({
    propTypes: {
        value: PropTypes.string,
        errorText: PropTypes.string,
        groupIds: PropTypes.array,
        change: PropTypes.func,
        cancel: PropTypes.func,
        save: PropTypes.func,
    },

    componentWillMount() {
        this.props.fetchGroupIds();
    },

    onSave(event) {
        event.preventDefault();
        const { value, errorText, save } = this.props;
        if (errorText.length > 1) {
            this.fieldRef.focus();
        } else {
            save(value);
        }
    },

    saveRefAndFocus(ref) {
        if (ref) {
            this.fieldRef = ref;
            ref.focus();
        }
    },

    render() {
        const { value, errorText, groupIds, change, cancel } = this.props;
        return (
            <AddSubGroup
                value={value}
                errorText={errorText}
                groupIds={groupIds}
                change={change}
                cancel={cancel}
                save={this.onSave}
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
