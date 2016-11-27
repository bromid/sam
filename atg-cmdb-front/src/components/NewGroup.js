import React, { PropTypes } from 'react';
import { withRouter, browserHistory } from 'react-router';
import isEmpty from 'lodash/isEmpty';
import { removeEmptyFields } from '../helpers';
import RaisedButton from 'material-ui/RaisedButton';
import FlatButton from 'material-ui/FlatButton';
import TextField from 'material-ui/TextField';
import * as groupValidators from '../validators/groupValidators';

const NewGroup = React.createClass({
    propTypes: {
        onCreate: PropTypes.func.isRequired,
        onCancel: PropTypes.func,
    },

    getInitialState() {
        return {
            id: '',
            idErrorText: '',
            name: '',
            nameErrorText: '',
            description: '',
            descriptionErrorText: '',
        };
    },

    componentDidMount() {
        this.refs.get('id').focus();
    },

    handleChangeId(event) {
        const id = event.target.value.trim().toLowerCase();
        const error = groupValidators.id(id);
        this.setState({ id, idErrorText: error });
    },

    handleChangeName(event) {
        const name = event.target.value;
        const error = groupValidators.name(name);
        this.setState({ name, nameErrorText: error });
    },

    handleChangeDescription(event) {
        const description = event.target.value;
        const error = groupValidators.description(description);
        this.setState({ description, descriptionErrorText: error });
    },

    handleCreate() {
        const { id, name, description } = this.state;
        const group = {
            id,
            name: name.trim(),
            description: description.trim(),
        };

        const errors = groupValidators.group(group);

        if (errors.hasError) {
            this.refs.get(errors.first).focus();
            this.setState(errors.text);
        } else {
            const groupNoEmptyFields = removeEmptyFields(group);
            this.props.onCreate(groupNoEmptyFields, () => browserHistory.push(`/group/${id}`));
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
        const {
            id, idErrorText,
            name, nameErrorText,
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
                        label="Create group"
                        secondary={true}
                        onTouchTap={this.handleCreate}
                    />
                </div>
            </form>
        );
    },
});

export default withRouter(NewGroup);
