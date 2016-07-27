import React from 'react';
import { Link } from 'react-router';
import { connect } from 'react-redux';
import { List, ListItem } from 'material-ui/List';
import LoadingIndicator from './LoadingIndicator';
import { serverName, serverLink } from './Server';

const Server = ({ server }) => (
    <Link to={serverLink(server)}>
        <ListItem primaryText={serverName(server)} secondaryText={server.description} />
    </Link>
);

export const Servers = ({ servers, header }) => {
    if (!servers) return <p>No servers</p>;
    return (
        <List>
            {header}
            {servers.map((server) => (
                <Server key={serverName(server)} server={server} />
            ))}
        </List>
    );
};

const ServerListContainer = ({ servers, isLoading }) => {
    if (isLoading) return <LoadingIndicator />;
    return <Servers servers={servers} header={<h2>Servers</h2>} />;
};

const mapStateToProps = ({ serverList, serverListIsPending }) => ({
    servers: serverList.items,
    isLoading: serverListIsPending,
});
export default connect(mapStateToProps)(ServerListContainer);
