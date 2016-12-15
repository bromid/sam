import React from 'react';
import FlatButton from 'material-ui/FlatButton';
import RaisedButton from 'material-ui/RaisedButton';

const SaveCancelForm = (props) => {
    const {
        children, save, cancel, columnStyle = false,
    } = props;

    const formStyleRow = {
        flex: 1,
        display: 'flex',
        flexDirection: 'row',
        alignItems: 'baseline',
        marginBottom: 10,
    };

    const formStyleColumn = {
        flex: 1,
        display: 'flex',
        flexDirection: 'column',
        alignItems: 'stretch',
    };

    return (
        <form style={(columnStyle) ? formStyleColumn : formStyleRow} onSubmit={save}>
            {children}
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
export default SaveCancelForm;
