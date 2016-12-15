import React from 'react';
import IconButton from 'material-ui/IconButton';
import DeleteIcon from 'material-ui/svg-icons/action/delete';

const DeleteButton = ({ style, onClick }) => (
    <IconButton
        style={{ ...style, zIndex: 2000 }}
        tooltip="Delete"
        tooltipPosition="bottom-center"
        onTouchTap={onClick}
    >
        <DeleteIcon />
    </IconButton>
);
export default DeleteButton;
