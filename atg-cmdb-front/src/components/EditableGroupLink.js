import React, { PropTypes } from 'react';
import { connect } from 'react-redux';
import { Link } from 'react-router';
import AutoComplete from 'material-ui/AutoComplete';
import SaveCancelForm from '../components/SaveCancelForm';
import EditIconButton from '../components/EditIconButton';
import { State, AllStates, isShowEditForm } from '../components/EditState';
import * as groupActions from '../actions/groupActions';
import { fromGroup } from '../reducers';

const EditableGroupLink = (props) => {
    const { group, edit, state } = props;

    if (group) {
        return (
            <div className="editIconWrapper">
                <Link to={`/group/${group.id}`}>{group.name}</Link>
                <EditIconButton
                    edit={edit}
                    state={state}
                    style={{ position: 'absolute', top: 15 }}
                />
            </div>
        );
    }

    if (state !== State.readonly) {
        return (
            <a href="#" onTouchTap={edit}>Assign to group</a>
        );
    }
    return null;
};

const EditableGroupLinkContainer = React.createClass({
    propTypes: {
        value: PropTypes.string.isRequired,
        state: PropTypes.oneOf(AllStates).isRequired,
        group: PropTypes.object,
        groupIds: PropTypes.array,
        errorText: PropTypes.string,
        edit: PropTypes.func,
        cancel: PropTypes.func,
        save: PropTypes.func,
        change: PropTypes.func,
    },

    componentWillReceiveProps(newProps) {
        const { state, fetchGroupIds } = this.props;
        if (isShowEditForm(newProps.state) && !isShowEditForm(state)) {
            fetchGroupIds();
        }
    },

    handleSave(event) {
        const { errorText = '', save } = this.props;
        if (errorText.length > 1) {
            this.fieldRef.focus();
        }
        save(event);
    },

    render() {
        const {
            group, groupIds = [], value, state, errorText, change, cancel, edit,
        } = this.props;

        return (
            isShowEditForm(state) ?
                <SaveCancelForm cancel={cancel} save={this.handleSave}>
                    <AutoComplete
                        searchText={value}
                        errorText={errorText}
                        onUpdateInput={change}
                        onNewRequest={change}
                        dataSource={groupIds}
                        id="groupInput"
                        hintText="Group"
                        ref={(ref) => (this.fieldRef = ref)}
                    />
                </SaveCancelForm> :
                <EditableGroupLink
                    group={group}
                    edit={edit}
                    state={state}
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
export default connect(mapStateToProps, Actions)(EditableGroupLinkContainer);
