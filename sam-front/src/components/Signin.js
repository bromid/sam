import React, { PropTypes } from 'react';
import { connect } from 'react-redux';
import keys from 'lodash/keys';
import isFunction from 'lodash/isFunction';
import * as LOG from '../helpers/log';
import * as authActions from '../actions/authActions';
import { SIGNIN_CODE_MESSAGE } from '../constants';
import { fromAuth } from '../reducers';

const signinWindowOptions = {
    toolbar: 'no',
    location: 'no',
    directories: 'no',
    status: 'no',
    menubar: 'no',
    scrollbars: 'yes',
    resizable: 'yes',
    width: 1024,
    height: 768,
    top: (o, w) => ((w.innerHeight - o.height) / 2) + w.screenY,
    left: (o, w) => ((w.innerWidth - o.width) / 2) + w.screenX,
};

const createOptions = () => (
    keys(signinWindowOptions)
        .map((key) => {
            const value = signinWindowOptions[key];
            return `${key}=${isFunction(value) ? value(signinWindowOptions, window) : value}`;
        }).join(',')
);

const SigninContainer = React.createClass({
    propTypes: {
        signinWindowRequest: PropTypes.object,
        signinIframeRequest: PropTypes.object,
    },

    componentWillMount() {
        const { signinWindowRequest } = this.props;
        this.showWindow(signinWindowRequest);
    },

    componentDidMount() {
        window.addEventListener('message', this.handleMessage);
    },

    componentWillReceiveProps(nextProps) {
        const { signinWindowRequest } = nextProps;
        this.showWindow(signinWindowRequest);
    },

    componentWillUnmount() {
        window.removeEventListener('message', this.handleMessage);
    },

    handleMessage(event) {
        LOG.debug('Received sign-in message', event.data, event.origin);

        const allowedOrigin = window.samSettings.oauth.origin;
        if (event.origin === allowedOrigin) {
            const { type, code, state } = event.data;
            if (type === SIGNIN_CODE_MESSAGE) {
                this.props.verifyOAuthCode(code, state);
            }
        }
    },

    pollWindowClosed(windowHandle) {
        const poll = window.setInterval(() => {
            if (!windowHandle || windowHandle.closed) {
                window.clearInterval(poll);
                this.closeWindow();
                this.props.signinWindowClosed();
            }
        }, 1000);
    },

    showWindow(request) {
        if (request) {
            this.openWindow(request);
        } else {
            this.closeWindow();
        }
    },

    openWindow(request) {
        if (!this.windowHandle) {
            this.windowHandle = window.open(request.url, 'signin', createOptions());
            this.pollWindowClosed(this.windowHandle);
        }
    },

    closeWindow() {
        if (this.windowHandle) {
            this.windowHandle.close();
            this.windowHandle = null;
        }
    },

    render() {
        const { signinIframeRequest } = this.props;
        if (signinIframeRequest) {
            return (
                <iframe
                    src={signinIframeRequest.url}
                    onLoad={this.props.signinIframeLoaded}
                    style={{ display: 'none' }}
                />
            );
        }
        return <div />;
    },
});

const mapStateToProps = (state) => ({
    signinWindowRequest: fromAuth.getSigninWindowRequest(state),
    signinIframeRequest: fromAuth.getSigninIframeRequest(state),
});

const Actions = {
    verifyOAuthCode: authActions.verifyOAuthCode,
    signinIframeLoaded: authActions.signinIframeLoaded,
    signinWindowClosed: authActions.signinWindowClosed,
};
export default connect(mapStateToProps, Actions)(SigninContainer);
