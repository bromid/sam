import React from 'react';
import IconButton from 'material-ui/IconButton';
import RefreshIcon from 'material-ui/svg-icons/navigation/refresh';

const RefreshButton = ({ style, onRefresh }) => (
    <IconButton
        style={{ ...style, zIndex: 2000 }}
        tooltip="Refresh"
        onTouchTap={onRefresh}
    >
        <RefreshIcon />
    </IconButton>
);
export default RefreshButton;
