import React from 'react';
import IconButton from 'material-ui/IconButton';
import RefreshIcon from 'material-ui/svg-icons/navigation/refresh';

const RefreshButton = ({ style, onClick }) => (
    <IconButton
        style={{ ...style, zIndex: 2000 }}
        tooltip="Refresh"
        tooltipPosition="bottom-center"
        onTouchTap={onClick}
    >
        <RefreshIcon />
    </IconButton>
);
export default RefreshButton;
