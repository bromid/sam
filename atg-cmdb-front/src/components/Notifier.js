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
        return {
            notification: getNotification({}),
            open: false,
        };
    },

    componentWillReceiveProps({ notification }) {
        const newNotification = getNotification(notification);
        const lastId = this.state.notification.id;

        if (!isEmpty(newNotification.message) && newNotification.id !== lastId) {
            this.setState({
                notification: newNotification,
                open: true,
            });
        }
    },

    requestClose() {
        this.setState({ open: false });
    },

    render() {
        const { open, notification: { duration, message, action } } = this.state;
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
