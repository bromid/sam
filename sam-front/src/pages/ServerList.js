import React from 'react';
import { Link } from 'react-router';
import { connect } from 'react-redux';
import RaisedButton from 'material-ui/RaisedButton';
import { flexWrapperStyle } from '../style';
import LoadingIndicator from '../components/LoadingIndicator';
import { ServerList } from '../components/ServerList';
import { fromServer, fromAuth } from '../reducers';

const ServerListPage = ({ servers, isAuthenticated }) => (
    <div>
        <div style={{ ...flexWrapperStyle, alignItems: 'center' }}>
            <div style={{ flex: 1 }}>
                <h2>Servers</h2>
            </div>
            {isAuthenticated &&
                <Link to="/server/new">
                    <RaisedButton
                        label="Add server"
                        style={{ borderRadius: 3 }}
                    />
                </Link>
            }
        </div>
        <ServerList servers={servers} />
    </div>
);

const ServerListPageContainer = ({ isLoading, isAuthenticated, servers }) => {
    if (isLoading) return <LoadingIndicator />;
    return <ServerListPage servers={servers} isAuthenticated={isAuthenticated} />;
};

const mapStateToProps = (state) => ({
    servers: fromServer.getList(state),
    isLoading: fromServer.getListIsPending(state),
    isAuthenticated: fromAuth.getIsAuthenticated(state),
});
export default connect(mapStateToProps)(ServerListPageContainer);
