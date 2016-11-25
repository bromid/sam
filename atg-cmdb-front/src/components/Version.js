import React from 'react';
import { connect } from 'react-redux';
import { Link } from 'react-router';
import LoadingIndicator from './LoadingIndicator';
import { fromInfo } from '../reducers';

const style = {
    position: 'relative',
    padding: 16,
    fontSize: 14,
    lineHeight: 0.7,
    minHeight: 45,
};

const Info = ({ info, isLoading }) => {
    if (isLoading) return <LoadingIndicator size={30} />;
    return (
        <div>
            <p>Version: <Link to="/release-notes">{info.version}</Link></p>
            <a href="/docs">API documentation</a>
        </div>
    );
};

const InfoContainer = ({ info, isLoading }) => (
    <div style={style}>
        <Info info={info} isLoading={isLoading} />
    </div>
);

const mapStateToProps = (state) => ({
    info: fromInfo.getData(state),
    isLoading: fromInfo.getIsPending(state),
});
export default connect(mapStateToProps)(InfoContainer);
