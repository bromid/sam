import React from 'react';
import CircularProgress from 'material-ui/CircularProgress';

const loadingStyle = {
    position: 'absolute',
    left: '50%',
    top: '50%',
    transform: 'translate(-50%, -50%)',
};

export default function () {
    return (
        <div style={loadingStyle}>
            <CircularProgress size={2} />
        </div>
    );
}
