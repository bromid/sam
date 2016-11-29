import React from 'react';
import { white } from 'material-ui/styles/colors';
import IconButton from 'material-ui/IconButton';
import ExpandMoreIcon from 'material-ui/svg-icons/navigation/expand-more';
import ExpandLessIcon from 'material-ui/svg-icons/navigation/expand-less';

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
export default Icon;
