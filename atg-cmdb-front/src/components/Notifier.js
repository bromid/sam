import React, { PropTypes } from 'react';
import isEmpty from 'lodash/isEmpty';
import isFunction from 'lodash/isFunction';
import Snackbar from 'material-ui/Snackbar';

const getNotification = (param) => {
    const notification = isFunction(param) ? param() : param;
    return {
        message: '',
        duration: 2000,
        action: {},
        ...notification,
    };
};

const Notifier = React.createClass({
    propTypes: {
        notification: PropTypes.oneOfType([PropTypes.object, PropTypes.func]).isRequired,
    },

    getInitialState() {
        const notification = getNotification(this.props.notification);
        return {
            ...notification,
            open: false,
        };
    },

    componentWillReceiveProps({ notification }) {
        const newNotification = getNotification(notification);
        const newMessage = newNotification.message;

        if (!isEmpty(newMessage) && newMessage !== this.state.message) {
            this.setState({
                ...newNotification,
                open: true,
            });
        } else {
            this.setState({
                message: newNotification.message,
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
            action={action.name}
            autoHideDuration={duration}
            onRequestClose={this.requestClose}
        />);
    },
});

export default Notifier;
