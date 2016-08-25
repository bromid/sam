import React from 'react';
import isFunction from 'lodash/isFunction';
import TextField from 'material-ui/TextField';
import FlatButton from 'material-ui/FlatButton';
import RaisedButton from 'material-ui/RaisedButton';

const PersistableField = (props) => {
    const { id, value, errorText, change, save, cancel, fieldRef, multiLine = false } = props;

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

    const saveFieldRef = (ref) => {
        if (isFunction(fieldRef)) {
            fieldRef(ref);
        }
    };

    return (
        <form style={(multiLine) ? formStyleMultiLine : formStyleSingleLine} onSubmit={save}>
            <TextField
                id={id}
                value={value}
                errorText={errorText}
                textareaStyle={{ minHeight: 150 }}
                fullWidth={true}
                multiLine={multiLine}
                onChange={change}
                ref={saveFieldRef}
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
};
export default PersistableField;
