import React from 'react';
import { white } from 'material-ui/styles/colors';
import AppBar from 'material-ui/AppBar';
import IconButton from 'material-ui/IconButton';
import ExpandMoreIcon from 'material-ui/svg-icons/navigation/expand-more';
import ExpandLessIcon from 'material-ui/svg-icons/navigation/expand-less';
import moment from 'moment';
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

const Icon = ({ open, toggleState }) => {
    if (open) {
        return (
            <IconButton
                tooltip="Collapse"
                onTouchTap={toggleState}
                children={<ExpandLessIcon color={white} />}
            />
        );
    }

    return (
        <IconButton
            tooltip="Expand"
            onTouchTap={toggleState}
            children={<ExpandMoreIcon color={white} />}
        />
    );
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
        height: open ? 'auto' : 0,
        margin: open ? '5px 16px' : '0 16px',
    };

    const wrapperStyle = {
        ...borderStyle,
        alignSelf: 'flex-start',
        minWidth: 250,
        margin: '15px 0 15px 15px',
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
