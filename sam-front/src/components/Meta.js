import React from 'react';
import AppBar from 'material-ui/AppBar';
import moment from 'moment';
import Icon from './ExpandCollapseIcon';
import { borderStyle } from '../style';

const appBarStyle = {
    borderRadius: 3,
    width: '102%',
    marginLeft: '-1%',
    padding: '0 24px 0 16px',
};

const appBarTitleStyle = {
    fontSize: 14,
    fontWeight: 500,
    height: 48,
    lineHeight: '48px',
};

const timeSince = (time) => {
    if (!time) return null;
    return ` (${moment(time).toNow(true)} ago)`;
};

export default function Meta(props) {
    if (!props.meta) return null;

    const {
        open, toggle,
        meta: {
            created, createdBy, updated, updatedBy, refreshed, refreshedBy,
        },
    } = props;

    const listStyle = {
        overflow: 'hidden',
        maxHeight: open ? 300 : 0,
        margin: open ? '5px 16px' : '0 16px',
        whiteSpace: open ? 'inherit' : 'nowrap',
        transition: 'all 350ms ease-in-out 100ms',
    };

    const wrapperStyle = {
        ...borderStyle,
        overflow: 'hidden',
        alignSelf: 'flex-start',
        width: (open) ? 270 : 120,
        margin: '15px 0 15px 15px',
        transition: 'all 350ms ease-in-out 0ms',
    };

    return (
        <div style={wrapperStyle}>
            <AppBar
                title="META"
                style={appBarStyle}
                titleStyle={appBarTitleStyle}
                iconStyleRight={{ marginTop: 0 }}
                showMenuIconButton={false}
                iconElementRight={<Icon open={open} toggleState={toggle} />}
            />
            <dl style={listStyle}>
                <dt>Created</dt>
                <dd>{createdBy}{timeSince(created)}</dd>
                <dt>Updated</dt>
                <dd>{updatedBy}{timeSince(updated)}</dd>
                <dt>Refreshed</dt>
                <dd>{refreshedBy}{timeSince(refreshed)}</dd>
            </dl>
        </div>
    );
}
