import React from 'react';
import IconButton from 'material-ui/IconButton';
import DeleteIcon from 'material-ui/svg-icons/action/delete';

const DeleteButton = ({ style, onDelete }) => (
    <IconButton
        style={{ ...style, zIndex: 2000 }}
        tooltip="Delete"
        onTouchTap={onDelete}
    >
        <DeleteIcon />
    </IconButton>
);
export default DeleteButton;
