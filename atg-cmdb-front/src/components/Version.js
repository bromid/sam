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
                <p>Version: {info.version}</p>
                <Link to="/release-notes">Release notes</Link>
            </div>
        );
    },
});

function mapStateToProps(state) {
    const { info, infoIsLoading } = state;
    return {
        info,
        isLoading: infoIsLoading || infoIsLoading === null,
    };
}

export default connect(mapStateToProps, Actions)(InfoContainer);
