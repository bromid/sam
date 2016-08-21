import React from 'react';
import { blue800, red800 } from 'material-ui/styles/colors';
import IconButton from 'material-ui/IconButton';
import RefreshIndicator from 'material-ui/RefreshIndicator';
import EditIcon from 'material-ui/svg-icons/image/edit';
import ErrorIcon from 'material-ui/svg-icons/alert/error';

export default function EditIconButton({ edit, state, style }) {
    if (state === 'readonly') return null;
    if (state === 'saving') {
        return (
            <div style={{ ...style, display: 'inline-flex' }}>
                <div style={{ position: 'relative', width: 40, height: 40 }}>
                    <RefreshIndicator
                        size={40}
                        left={0}
                        top={0}
                        status="loading"
                        style={{ boxShadow: 'inherit' }}
                    />
                </div>
            </div>
        );
    }
    if (state === 'failed') {
        return (
            <IconButton
                tooltip="Edit"
                onTouchTap={edit}
                style={{ ...style, padding: 0 }}
            >
                <ErrorIcon color={red800} />
            </IconButton>
        );
    }
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
