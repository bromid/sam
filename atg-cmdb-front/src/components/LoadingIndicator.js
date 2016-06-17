import React, {PropTypes} from 'react';
import CircularProgress from 'material-ui/CircularProgress';

const loadingStyle = {
    position: 'absolute',
    left: '50%',
    top: '50%',
    transform: 'translate(-50%, -50%)'
};

const LoadingIndicator = React.createClass({
    render() {
        return (
            <div style={loadingStyle}>
                <CircularProgress size={2} />
            </div>
        )
    }
});

export default LoadingIndicator;