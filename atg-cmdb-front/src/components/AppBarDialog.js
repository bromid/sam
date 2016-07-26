import React from 'react';
import Dialog from 'material-ui/Dialog';
import AppBar from 'material-ui/AppBar';

const AppBarDialog = ({ title, open, actions, contentStyle, onRequestClose, children }) => {
    const titleBar = (
        <AppBar
            title={title}
            style={{
                borderRadius: 2,
                padding: '0 24px 0 16px',
            }}
            titleStyle={{
                fontSize: 14,
                fontWeight: 500,
                height: 48,
                lineHeight: '48px',
            }}
            showMenuIconButton={false}
        />
    );

    return (
        <Dialog
            title={titleBar}
            open={open}
            actions={actions}
            contentStyle={contentStyle}
            onRequestClose={onRequestClose}
            autoScrollBodyContent={true}
        >
            <div style={{ marginTop: 20 }}>
                {children}
            </div>
        </Dialog>
    );
};
export default AppBarDialog;
