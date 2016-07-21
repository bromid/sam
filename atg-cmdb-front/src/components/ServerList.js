import React from 'react';
import { Link } from 'react-router';
import { connect } from 'react-redux';
import * as Actions from '../actions/serverActions';
import { List, ListItem } from 'material-ui/List';
import LoadingIndicator from './LoadingIndicator';

function Server({ server }) {
    return (
        <ListItem
            primaryText={
                <Link to={`/server/${server.environment}/${server.hostname}`}>
                    {`${server.hostname}@${server.environment}`}
                </Link>
            }
            secondaryText={server.description}
        />
    );
}

function Servers({ servers }) {
    return (
        <List>
            <h2>Servers</h2>
            {servers.map(server => (
                <Server key={server.hostname + server.environment} server={server} />
            ))}
        </List>
    );
}
const ServerListContainer = React.createClass({

    componentDidMount() {
        this.props.fetchServerList();
    },

    render() {
        const { servers, isLoading } = this.props;
        if (isLoading) return <LoadingIndicator />;
        if (!servers) return <p>No results</p>;
        return (
            <Servers servers={servers} />
        );
    },
});

function mapStateToProps(state) {
    const { serverList, serverListIsPending } = state;
    return {
        servers: serverList.items,
        isLoading: serverListIsPending || serverListIsPending === null,
    };
}
export default connect(mapStateToProps, Actions)(ServerListContainer);
