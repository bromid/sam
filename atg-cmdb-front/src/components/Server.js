import React from 'react';
import { connect } from 'react-redux';
import * as Actions from '../actions/serverActions';
import { List, ListItem } from 'material-ui/List';
import Subheader from 'material-ui/Subheader';
import Attributes from './Attributes';
import LoadingIndicator from "./LoadingIndicator";

const ServersContainer = React.createClass({

    componentDidMount() {
        this.props.fetchServers();
    },

    render() {
        const {servers, isLoading} = this.props;
        if (isLoading) return <LoadingIndicator />;
        if (!servers) return <p>No results</p>;
        return <p>Server</p>;
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
