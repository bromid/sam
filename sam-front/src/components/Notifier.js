import React, { PropTypes } from 'react';
import isEmpty from 'lodash/isEmpty';
import isFunction from 'lodash/isFunction';
import RaisedButton from 'material-ui/RaisedButton';
import Snackbar from 'material-ui/Snackbar';
import AppBarDialog from './AppBarDialog';

const getNotification = (param) => {
    const notification = isFunction(param) ? param() : param;
    return {
        message: '',
        duration: 3500,
        action: {},
        ...notification,
    };
};

const Notifier = React.createClass({
    propTypes: {
        notification: PropTypes.oneOfType([PropTypes.object, PropTypes.func]).isRequired,
    },

    getInitialState() {
        return {
            notification: getNotification({}),
            notificationOpen: false,
            dialogOpen: false,
        };
    },

    componentWillReceiveProps({ notification }) {
        const newNotification = getNotification(notification);
        const lastId = this.state.notification.id;

        if (!isEmpty(newNotification.message) && newNotification.id !== lastId) {
            this.setState({
                notification: newNotification,
                notificationOpen: true,
            });
        }
    },

    showDialog(content, title = 'ERROR') {
        this.setState({
            dialogOpen: true,
            dialogContent: content,
            dialogTitle: title,
            notificationOpen: false,
        });
    },

    closeDialog() {
        this.setState({
            dialogOpen: false,
        });
    },

    closeNotification() {
        this.setState({
            notificationOpen: false,
        });
    },

    render() {
        const {
            notificationOpen, notification: { duration, message, action },
            dialogOpen, dialogTitle, dialogContent,
        } = this.state;

        const actions = [
            <RaisedButton
                label="Close"
                secondary={true}
                onTouchTap={this.closeDialog}
            />,
        ];

        const onNotificationTouchTap = (action.onTouchTap) ? () => {
            action.onTouchTap(this.showDialog);
        } : undefined;

        return (
            <div>
                <Snackbar
                    open={notificationOpen}
                    message={message}
                    action={action.name}
                    onActionTouchTap={onNotificationTouchTap}
                    autoHideDuration={duration}
                    onRequestClose={this.closeNotification}
                />
                <AppBarDialog
                    title={dialogTitle}
                    open={dialogOpen}
                    actions={actions}
                    onRequestClose={this.closeDialog}
                    contentStyle={{ minWidth: 500 }}
                    children={dialogContent}
                />
            </div>
        );
    },
});
export default Notifier;
