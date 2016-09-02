import React from 'react';
import { connect } from 'react-redux';
import { flexWrapperStyle } from '../style';
import LoadingIndicator from '../components/LoadingIndicator';
import { ServerList } from '../components/ServerList';
import { fromServer } from '../reducers';

const ServerListPage = ({ servers }) => (
    <div>
        <div style={{ ...flexWrapperStyle, alignItems: 'center' }}>
            <div style={{ flex: 1 }}>
                <h2>Servers</h2>
            </div>
        </div>
        <ServerList servers={servers} />
    </div>
);

const ServerListPageContainer = ({ servers, isLoading }) => {
    if (isLoading) return <LoadingIndicator />;
    return <ServerListPage servers={servers} />;
};

const mapStateToProps = (state) => ({
    servers: fromServer.getList(state),
    isLoading: fromServer.getListIsPending(state),
});
export default connect(mapStateToProps)(ServerListPageContainer);
