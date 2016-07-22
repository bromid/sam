import React from 'react';
import isFunction from 'lodash/isFunction';
import { blue800 } from 'material-ui/styles/colors';
import IconButton from 'material-ui/IconButton';
import EditIcon from 'material-ui/svg-icons/image/edit';

export default function EditIconButton({ style, edit }) {
    if (!isFunction(edit)) return null;
    return (
        <IconButton
            className="editIcon"
            tooltip="Edit"
            onTouchTap={edit}
            style={{ ...style, display: 'none', padding: 0 }}
        >
            <EditIcon color={blue800} />
        </IconButton>
    );
}
