import React, { PropTypes } from 'react';
import { withRouter } from 'react-router';
import isEmpty from 'lodash/isEmpty';
import RaisedButton from 'material-ui/RaisedButton';
import FlatButton from 'material-ui/FlatButton';
import TextField from 'material-ui/TextField';
import AutoComplete from 'material-ui/AutoComplete';
import { removeEmptyFields } from '../helpers';
import * as applicationValidators from '../validators/applicationValidators';

const NewApplication = React.createClass({
    propTypes: {
        onCreate: PropTypes.func.isRequired,
        groupIds: PropTypes.array.isRequired,
        onCancel: PropTypes.func,
    },

    getInitialState() {
        return {
            id: '',
            idErrorText: '',
            name: '',
            nameErrorText: '',
            group: '',
            groupErrorText: '',
            description: '',
            descriptionErrorText: '',
        };
    },

    componentDidMount() {
        this.refs.get('id').focus();
    },

    handleChangeId(event) {
        const id = event.target.value.trim().toLowerCase();
        const idErrorText = applicationValidators.id(id);
        this.setState({ id, idErrorText });
    },

    handleChangeName(event) {
        const name = event.target.value;
        const nameErrorText = applicationValidators.name(name);
        this.setState({ name, nameErrorText });
    },

    handleChangeGroup(groupId) {
        const group = groupId.trim().toLowerCase();
        const groupErrorText = applicationValidators.group(group);
        this.setState({ group, groupErrorText });
    },

    handleChangeDescription(event) {
        const description = event.target.value;
        const descriptionErrorText = applicationValidators.description(description);
        this.setState({ description, descriptionErrorText });
    },

    handleCreate() {
        const { id, name, group, description } = this.state;
        const application = {
            id, group,
            name: name.trim(),
            description: description.trim(),
        };

        const errors = applicationValidators.application(application);

        if (errors.hasError) {
            this.refs.get(errors.first).focus();
            this.setState(errors.text);
        } else {
            const applicationNoEmptyFields = removeEmptyFields(application);
            this.props.onCreate(applicationNoEmptyFields);
        }
    },

    handleCancel() {
        const { onCancel, router } = this.props;
        if (onCancel) {
            onCancel();
        } else {
            router.goBack();
        }
    },

    addField(id, ref) {
        if (ref) {
            if (isEmpty(this.refs)) {
                this.refs = new Map();
            }
            this.refs.set(id, ref);
        }
    },

    render() {
        const { groupIds } = this.props;

        const {
            id, idErrorText,
            name, nameErrorText,
            group, groupErrorText,
            description, descriptionErrorText,
        } = this.state;

        const formStyle = {
            flex: 1,
            display: 'flex',
            flexDirection: 'column',
            alignItems: 'stretch',
        };

        return (
            <form style={formStyle}>
                <TextField
                    value={id}
                    errorText={idErrorText}
                    onChange={this.handleChangeId}
                    floatingLabelText="Id*"
                    fullWidth={true}
                    ref={(ref) => this.addField('id', ref)}
                />
                <TextField
                    value={name}
                    errorText={nameErrorText}
                    onChange={this.handleChangeName}
                    floatingLabelText="Name*"
                    fullWidth={true}
                    ref={(ref) => this.addField('name', ref)}
                />
                <AutoComplete
                    searchText={group}
                    errorText={groupErrorText}
                    onUpdateInput={this.handleChangeGroup}
                    onNewRequest={this.handleChangeGroup}
                    dataSource={groupIds}
                    floatingLabelText="Group id"
                    fullWidth={true}
                    ref={(ref) => this.addField('group', ref)}
                />
                <TextField
                    value={description}
                    errorText={descriptionErrorText}
                    onChange={this.handleChangeDescription}
                    floatingLabelText="Description"
                    multiLine={true}
                    fullWidth={true}
                    ref={(ref) => this.addField('description', ref)}
                    textareaStyle={{ minHeight: 150 }}
                />
                <div style={{ display: 'flex', marginTop: 16 }}>
                    <span style={{ flex: 1 }}>* indicates required field</span>
                    <FlatButton
                        label="Cancel"
                        secondary={false}
                        onTouchTap={this.handleCancel}
                    />
                    <RaisedButton
                        label="Create application"
                        secondary={true}
                        onTouchTap={this.handleCreate}
                    />
                </div>
            </form>
        );
    },
});

export default withRouter(NewApplication);
