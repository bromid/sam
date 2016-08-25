import React from 'react';
import { connect } from 'react-redux';
import isEmpty from 'lodash/isEmpty';
import RaisedButton from 'material-ui/RaisedButton';
import TextField from 'material-ui/TextField';
import AutoComplete from 'material-ui/AutoComplete';
import { removeEmptyFields } from '../helpers';
import * as applicationActions from '../actions/applicationActions';
import * as applicationValidators from '../validators/applicationValidators';

const NewApplicationContainer = React.createClass({

    getInitialState() {
        return {
            id: '',
            idErrorText: '',
            name: '',
            nameErrorText: '',
            description: '',
            descriptionErrorText: '',
            group: '',
            groupErrorText: '',
        };
    },

    onChangeId(event) {
        const id = event.target.value.trim();
        const idErrorText = applicationValidators.id(id);
        this.setState({ id, idErrorText });
    },

    onChangeName(event) {
        const name = event.target.value.trim();
        const nameErrorText = applicationValidators.name(name);
        this.setState({ name, nameErrorText });
    },

    onChangeDescription(event) {
        const description = event.target.value.trim();
        const descriptionErrorText = applicationValidators.description(description);
        this.setState({ description, descriptionErrorText });
    },

    onChangeGroup(groupName) {
        const group = groupName.trim();
        const groupErrorText = applicationValidators.group(group);
        this.setState({ group, groupErrorText });
    },

    onCreate() {
        const { id, name, description, group } = this.state;
        const application = { id, name, description, group };

        const errors = applicationValidators.application(application);

        if (errors.hasError) {
            this.refs.get(errors.first).focus();
            this.setState(errors.text);
        } else {
            const applicationNoEmptyFields = removeEmptyFields(application);
            this.props.createApplication(applicationNoEmptyFields);
        }
    },

    addField(id, ref) {
        if (isEmpty(this.refs)) {
            this.refs = new Map();
        }
        this.refs.set(id, ref);
    },

    render() {
        const {
            id, idErrorText,
            name, nameErrorText,
            description, descriptionErrorText,
            group, groupErrorText,
        } = this.state;

        const formStyle = {
            flex: 1,
            display: 'flex',
            flexDirection: 'column',
            alignItems: 'stretch',
        };

        const groupIds = [];

        return (
            <div>
                <h2>New application</h2>
                <form style={formStyle}>
                    <TextField
                        value={id}
                        errorText={idErrorText}
                        onChange={this.onChangeId}
                        floatingLabelText="Id*"
                        fullWidth={true}
                        ref={(ref) => this.addField('id', ref)}
                    />
                    <TextField
                        value={name}
                        errorText={nameErrorText}
                        onChange={this.onChangeName}
                        floatingLabelText="Name*"
                        fullWidth={true}
                        ref={(ref) => this.addField('name', ref)}
                    />
                    <AutoComplete
                        searchText={group}
                        errorText={groupErrorText}
                        onUpdateInput={this.onChangeGroup}
                        onNewRequest={this.onChangeGroup}
                        dataSource={groupIds}
                        floatingLabelText="Group"
                        fullWidth={true}
                        ref={(ref) => this.addField('group', ref)}
                    />
                    <TextField
                        value={description}
                        errorText={descriptionErrorText}
                        onChange={this.onChangeDescription}
                        floatingLabelText="Description"
                        multiLine={true}
                        fullWidth={true}
                        ref={(ref) => this.addField('description', ref)}
                        textareaStyle={{ minHeight: 150 }}
                    />
                    <div style={{ display: 'flex', marginTop: 16 }}>
                        <span style={{ flex: 1 }}>* indicates required field</span>
                        <RaisedButton
                            label="Create application"
                            secondary={true}
                            onTouchTap={this.onCreate}
                        />
                    </div>
                </form>
            </div>
        );
    },
});

const Actions = {
    createApplication: applicationActions.createApplication,
};
export default connect(null, Actions)(NewApplicationContainer);
