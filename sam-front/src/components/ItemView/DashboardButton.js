import React from 'react';
import IconButton from 'material-ui/IconButton';
import DashboardIcon from 'material-ui/svg-icons/action/dashboard';

const DashboardButton = ({ style, onClick, tooltip }) => (
    <IconButton
        style={{ ...style, zIndex: 2000 }}
        tooltip={tooltip}
        tooltipPosition="bottom-left"
        onTouchTap={onClick}
    >
        <DashboardIcon />
    </IconButton>
);
export default DashboardButton;
