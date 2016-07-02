import React from 'react';
import { Link } from 'react-router';
import { connect } from 'react-redux';
import * as Actions from '../actions/serverActions';
import { List, ListItem } from 'material-ui/List';
import LoadingIndicator from "./LoadingIndicator";

function Deployment({deployment}) {
    return (
        <ListItem
            primaryText={
                <Link to={`/application/${deployment.applicationLink.id}`}>
                    {`${deployment.applicationLink.name} (${deployment.version})`}
                </Link>
            }
        />
    );
}

const ServerContainer = React.createClass({

    componentDidMount() {
        const { environment, hostname } = this.props;
        this.props.fetchServer({ environment, hostname }) ;
    },

    componentWillReceiveProps(newProps) {
        const { environment, hostname } = this.props;
        const newEnvironment = newProps.environment;
        const newHostname = newProps.hostname;
        if (newEnvironment !== environment || newHostname !== hostname) {
            this.props.fetchServer({
                environment: newEnvironment,
                hostname: newHostname
            });
        }
    },

    render() {
        const { server, isLoading } = this.props;
        if (isLoading) return <LoadingIndicator />;
        if (!server.hostname) return <p>No results</p>;
        return (
            <div style={{ padding: '8px 0px' }}>
                <h2>{server.hostname}@{server.environment}</h2>
                <div style={{ margin: 16 }}>
                    <p>{server.description}</p>
                    <List>
                        <h3>Deployments</h3>
                        { server.deployments.map(deployment => (
                            <Deployment key={deployment.applicationLink.id} deployment={deployment} />
                        ))}
                    </List>
                </div>
            </div>
        );
    }
});

function mapStateToProps(state, props) {
    const { server, serverIsLoading } = state;
    const { params } = props;
    return {
        environment: params.environment,
        hostname: params.hostname,
        server: server,
        isLoading: serverIsLoading || serverIsLoading === null
    };
}
export default connect(mapStateToProps, Actions)(ServerContainer);
