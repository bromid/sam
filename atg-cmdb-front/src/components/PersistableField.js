import React from 'react';
import TextField from 'material-ui/TextField';
import FlatButton from 'material-ui/FlatButton';
import RaisedButton from 'material-ui/RaisedButton';

export default function TextFieldForm({ id, value, change, save, cancel, multiLine = false }) {
    const formStyleSingleLine = {
        flex: 1,
        display: 'flex',
        flexDirection: 'row',
        alignItems: 'baseline',
    };

    const formStyleMultiLine = {
        flex: 1,
        display: 'flex',
        flexDirection: 'column',
        alignItems: 'stretch',
    };

    return (
        <form style={(multiLine) ? formStyleMultiLine : formStyleSingleLine} onSubmit={save}>
            <TextField
                id={id}
                style={{ flex: 1 }}
                fullWidth={true}
                multiLine={multiLine}
                value={value}
                onChange={change}
            />
            <div style={{ display: 'flex', justifyContent: 'flex-end' }}>
                <FlatButton
                    label="Cancel"
                    secondary={false}
                    onTouchTap={cancel}
                />
                <RaisedButton
                    label="Save"
                    secondary={true}
                    primary={false}
                    onTouchTap={save}
                />
            </div>
        </form>
    );
}
