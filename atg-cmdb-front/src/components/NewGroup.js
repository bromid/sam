import React from 'react';
import { connect } from 'react-redux';
import pickBy from 'lodash/pickBy';
import RaisedButton from 'material-ui/RaisedButton';
import TextField from 'material-ui/TextField';
import * as groupActions from '../actions/groupActions';
import * as groupValidators from '../validators/groupValidators';

const NewGroupContainer = React.createClass({

    getInitialState() {
        return {
            id: '',
            idErrorText: '',
            name: '',
            nameErrorText: '',
            description: '',
            descriptionErrorText: '',
            refs: new Map(),
        };
    },

    onChangeId(event) {
        const id = event.target.value;
        const error = groupValidators.id(id);
        this.setState({ id, idErrorText: error });
    },

    onChangeName(event) {
        const name = event.target.value;
        const error = groupValidators.name(name);
        this.setState({ name, nameErrorText: error });
    },

    onChangeDescription(event) {
        const description = event.target.value;
        const error = groupValidators.description(description);
        this.setState({ description, descriptionErrorText: error });
    },

    onCreate() {
        const { id, name, description, refs } = this.state;
        const group = { id, name, description };

        const errors = groupValidators.group(group);

        if (errors.hasError) {
            refs.get(errors.first).focus();
            this.setState(errors.text);
        } else {
            const groupNoEmptyFields = pickBy(group, (x) => x);
            this.props.createGroup(groupNoEmptyFields);
        }
    },

    addField(id, ref) {
        this.state.refs.set(id, ref);
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
            <div>
                <h2>New group</h2>
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
                    <TextField
                        value={description}
                        errorText={descriptionErrorText}
                        onChange={this.onChangeDescription}
                        floatingLabelText="Description"
                        multiLine={true}
                        fullWidth={true}
                        ref={(ref) => this.addField('description', ref)}
                        style={{ minHeight: 150 }}
                    />
                    <div style={{ display: 'flex', marginTop: 16 }}>
                        <span style={{ flex: 1 }}>* indicates required field</span>
                        <RaisedButton
                            label="Create group"
                            secondary={true}
                            onTouchTap={this.onCreate}
                        />
                    </div>
                </form>
            </div>
        );
    },
});

const mapStateToProps = (state) => {
    const {
        metaOpen,
    } = state;
    return {
        metaOpen,
    };
};

const Actions = {
    createGroup: groupActions.createGroup,
};
export default connect(mapStateToProps, Actions)(NewGroupContainer);
