import React from 'react';
import { Link } from 'react-router';
import { List, ListItem } from 'material-ui/List';

export const serverName = (server) => (
    `${server.hostname}@${server.environment}`
);

export const serverLink = (server) => (
    `/server/${server.environment}/${server.hostname}`
);

const ServerListItem = ({ server }) => (
    <Link to={serverLink(server)}>
        <ListItem primaryText={serverName(server)} secondaryText={server.description} />
    </Link>
);

export const ServerList = ({ servers }) => {
    if (!servers) return <p>No servers</p>;
    return (
        <List>
            {servers.map((server) => (
                <ServerListItem key={serverName(server)} server={server} />
            ))}
        </List>
    );
};
