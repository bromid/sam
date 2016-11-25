import React from 'react';
import CircularProgress from 'material-ui/CircularProgress';

const loadingStyle = {
    zIndex: 5000,
    position: 'absolute',
    left: '50%',
    top: '50%',
    transform: 'translate(-50%, -50%)',
};

export default function ({ size = 120 }) {
    return (
        <div style={loadingStyle}>
            <CircularProgress size={size} />
        </div>
    );
}
