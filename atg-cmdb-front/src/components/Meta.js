import React from 'react';
import { white } from 'material-ui/styles/colors';
import Paper from 'material-ui/Paper';
import AppBar from 'material-ui/AppBar';
import IconButton from 'material-ui/IconButton';
import ExpandMoreIcon from 'material-ui/svg-icons/navigation/expand-more';
import ExpandLessIcon from 'material-ui/svg-icons/navigation/expand-less';
import moment from 'moment';

function timeSince(time) {
    if (!time) return null;
    return ` (${moment(time).toNow(true)} ago)`;
}

function Icon({ open, toggleState }) {
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
}

const Meta = React.createClass({

    getInitialState() {
        return {
            open: true,
        };
    },

    toggleOpen() {
        this.setState({
            open: !this.state.open,
        });
    },

    render() {
        if (!this.props.meta) return null;
        const { open } = this.state;
        const {
            meta: {
                created, createdBy, updated, updatedBy, refreshed, refreshedBy,
            },
        } = this.props;

        const listStyle = {
            overflow: 'hidden',
            height: open ? 'auto' : 0,
            margin: open ? '5px 16px' : '0 16px',
        };

        return (
            <Paper
                style={{ minWidth: 250, marginBottom: 30 }}
                zDepth={2}
                children={
                    <div>
                        <AppBar
                            title="Meta"
                            titleStyle={{ fontSize: 18, height: 48, lineHeight: '48px' }}
                            style={{ borderRadius: 2, padding: '0 24px 0 16px' }}
                            iconStyleRight={{ marginTop: 0 }}
                            showMenuIconButton={false}
                            iconElementRight={
                                <Icon open={open} toggleState={this.toggleOpen} />
                            }
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
                }
            />
        );
    },
});

export default Meta;
