import React from 'react';
import * as LOG from '../helpers/log';
import { SIGNIN_CODE_MESSAGE } from '../constants';

const OAuth = React.createClass({

    componentDidMount() {
        const { location: { query } } = this.props;
        if (query) {
            const parent = (window.opener) ? window.opener : window.parent;
            const origin = window.samSettings.oauth.origin;

            const msg = {
                type: SIGNIN_CODE_MESSAGE,
                code: query.code,
                state: query.state,
            };
            LOG.debug('Post sign-in message', msg, origin);
            parent.postMessage(msg, origin);
        }
        window.close();
    },

    render() {
        return <div>OAuth2 landing page.</div>;
    },
});
export default OAuth;
