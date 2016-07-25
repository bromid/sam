import React from 'react';
import { Link } from 'react-router';
import { connect } from 'react-redux';
import * as Actions from '../actions/serverActions';
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

const ServerListContainer = React.createClass({

    componentDidMount() {
        this.props.fetchServerList();
    },

    render() {
        const { servers, isLoading } = this.props;
        if (isLoading) return <LoadingIndicator />;
        return (
            <Servers servers={servers} header={<h2>Servers</h2>} />
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
