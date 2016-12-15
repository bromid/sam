import React from 'react';
import { connect } from 'react-redux';
import { withRouter } from 'react-router';
import isEmpty from 'lodash/isEmpty';
import RaisedButton from 'material-ui/RaisedButton';
import FlatButton from 'material-ui/FlatButton';
import TextField from 'material-ui/TextField';
import { removeEmptyFields } from '../helpers';
import * as serverActions from '../actions/serverActions';
import * as serverValidators from '../validators/serverValidators';

const NewServerContainer = React.createClass({

    getInitialState() {
        return {
            hostname: '',
            hostnameErrorText: '',
            environment: '',
            environmentErrorText: '',
            fqdn: '',
            fqdnErrorText: '',
            description: '',
            descriptionErrorText: '',
        };
    },

    componentDidMount() {
        this.refs.get('hostname').focus();
    },

    onChangeHostname(event) {
        const hostname = event.target.value.trim().toLowerCase();
        const hostnameErrorText = serverValidators.hostname(hostname);
        this.setState({ hostname, hostnameErrorText });
    },

    onChangeEnvironment(event) {
        const environment = event.target.value.trim().toLowerCase();
        const environmentErrorText = serverValidators.environment(environment);
        this.setState({ environment, environmentErrorText });
    },

    onChangeFqdn(event) {
        const fqdn = event.target.value.trim().toLowerCase();
        const fqdnErrorText = serverValidators.fqdn(fqdn);
        this.setState({ fqdn, fqdnErrorText });
    },

    onChangeDescription(event) {
        const description = event.target.value;
        const descriptionErrorText = serverValidators.description(description);
        this.setState({ description, descriptionErrorText });
    },

    onCreate() {
        const { hostname, environment, fqdn, description } = this.state;
        const server = {
            hostname, environment, fqdn,
            description: description.trim(),
        };

        const errors = serverValidators.server(server);

        if (errors.hasError) {
            this.refs.get(errors.first).focus();
            this.setState(errors.text);
        } else {
            const serverNoEmptyFields = removeEmptyFields(server);
            this.props.createServer(serverNoEmptyFields);
        }
    },

    onCancel() {
        this.props.router.goBack();
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
            hostname, hostnameErrorText,
            environment, environmentErrorText,
            fqdn, fqdnErrorText,
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
                <h2>New server</h2>
                <form style={formStyle}>
                    <TextField
                        value={hostname}
                        errorText={hostnameErrorText}
                        onChange={this.onChangeHostname}
                        floatingLabelText="Hostname*"
                        fullWidth={true}
                        ref={(ref) => this.addField('hostname', ref)}
                    />
                    <TextField
                        value={environment}
                        errorText={environmentErrorText}
                        onChange={this.onChangeEnvironment}
                        floatingLabelText="Environment*"
                        fullWidth={true}
                        ref={(ref) => this.addField('environment', ref)}
                    />
                    <TextField
                        value={fqdn}
                        errorText={fqdnErrorText}
                        onChange={this.onChangeFqdn}
                        floatingLabelText="Qualified domain name"
                        fullWidth={true}
                        ref={(ref) => this.addField('fqdn', ref)}
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
                        <FlatButton
                            label="Cancel"
                            secondary={false}
                            onTouchTap={this.onCancel}
                        />
                        <RaisedButton
                            label="Create server"
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
    createServer: serverActions.createServer,
};
export default connect(null, Actions)(withRouter(NewServerContainer));
