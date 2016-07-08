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

const ServerListContainer = React.createClass({

    componentDidMount() {
        this.props.fetchServerList();
    },

    render() {
        const { servers, isLoading } = this.props;
        if (isLoading) return <LoadingIndicator />;
        if (!servers) return <p>No results</p>;
        return (
            <List>
                <h2>Servers</h2>
                {servers.map(server => (
                    <Server key={server.hostname + server.environment} server={server} />
                ))}
            </List>
        );
    },
});

function mapStateToProps(state) {
    const { serverList, serverListIsLoading } = state;
    return {
        servers: serverList.items,
        isLoading: serverListIsLoading || serverListIsLoading === null,
    };
}
export default connect(mapStateToProps, Actions)(ServerListContainer);
