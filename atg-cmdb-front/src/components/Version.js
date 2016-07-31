import React from 'react';
import { connect } from 'react-redux';
import { Link } from 'react-router';
import LoadingIndicator from './LoadingIndicator';
import { fromInfo } from '../reducers';

const InfoContainer = ({ info, isLoading }) => {
    const style = {
        position: 'relative',
        padding: 16,
        fontSize: 14,
        lineHeight: 0.7,
    };

    if (isLoading) return <LoadingIndicator size={1} />;
    return (
        <div style={style}>
            <p>Version: <Link to="/release-notes">{info.version}</Link></p>
            <a href="/docs">API documentation</a>
        </div>
    );
};

const mapStateToProps = (state) => ({
    info: fromInfo.getData(state),
    isLoading: fromInfo.getIsPending(state),
});
export default connect(mapStateToProps)(InfoContainer);
