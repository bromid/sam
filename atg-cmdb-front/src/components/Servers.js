import React from 'react';
import { connect } from 'react-redux';
import * as Actions from '../actions/serverActions';
import { List, ListItem } from 'material-ui/List';
import Subheader from 'material-ui/Subheader';
import Attributes from './Attributes';
import LoadingIndicator from "./LoadingIndicator";

function Server({server}) {
    return (
        <ListItem
            primaryText={`${server.hostname}@${server.environment}`}
            secondaryText={server.description}
        />
    );
}

const ServersContainer = React.createClass({

    componentDidMount() {
        this.props.fetchServers();
    },

    render() {
        const {servers, isLoading} = this.props;
        if (isLoading) return <LoadingIndicator />;
        if (!servers) return <p>No results</p>;
        return <List>
            <h2 style={{margin: 15}}>Servers</h2>
            {servers.map(server => (
                <Server key={server.hostname + server.environment} server={server} />
            ))};
        </List>;
    }
});

function mapStateToProps(state, props) {
    const {servers, serversIsLoading} = state;
    return {
        servers: servers.items,
        isLoading: serversIsLoading || serversIsLoading === null
    };
}
export default connect(mapStateToProps, Actions)(ServersContainer);
