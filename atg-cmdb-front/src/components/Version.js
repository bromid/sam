import React from 'react';
import { Link } from 'react-router';
import * as Actions from '../actions/infoActions';
import LoadingIndicator from './LoadingIndicator';
import { connect } from 'react-redux';

const InfoContainer = React.createClass({

    componentDidMount() {
        this.props.fetchInfo();
    },

    render() {
        const { info, isLoading } = this.props;
        if (isLoading) return <LoadingIndicator size={1} />;
        return (
            <div style={{ position: 'relative', padding: 16, fontSize: 14, lineHeight: 0.7 }}>
                <p>Version: <Link to="/release-notes">{info.version}</Link></p>
                <a href="/docs">API documentation</a>
            </div>
        );
    },
});

function mapStateToProps(state) {
    const { info, infoIsPending } = state;
    return {
        info,
        isLoading: infoIsPending || infoIsPending === null,
    };
}

export default connect(mapStateToProps, Actions)(InfoContainer);
