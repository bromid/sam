import React from 'react';
import isEmpty from 'lodash/isEmpty';
import isFunction from 'lodash/isFunction';
import Snackbar from 'material-ui/Snackbar';

const Notifier = React.createClass({

    getInitialState() {
        return {
            open: false,
            message: '',
        };
    },

    componentWillReceiveProps({ notification }) {
        const notificationObj = isFunction(notification) ? notification() : notification;
        const { message: newMessage, duration = 2000, action = {} } = notificationObj;
        const emptyMessage = isEmpty(newMessage);
        if (!emptyMessage && newMessage !== this.state.message) {
            this.setState({
                duration,
                action,
                open: true,
                message: newMessage,
            });
        } else {
            this.setState({
                message: (emptyMessage) ? '' : newMessage,
            });
        }
    },

    requestClose() {
        this.setState({
            open: false,
        });
    },

    render() {
        const { open, duration, message, action } = this.state;
        return (<Snackbar
            open={open}
            message={message}
            action={(action) ? action.name : undefined}
            autoHideDuration={duration}
            onRequestClose={this.requestClose}
        />);
    },
});

export default Notifier;
