import React from 'react';
import { Link } from 'react-router';
import { List } from 'material-ui/List';
import Badge from 'material-ui/Badge';

const CountBadge = ({ children, title, primary, secondary }) => {
    const style = { padding: '12px' };
    const badgeStyle = { width: '16px', height: '16px', fontSize: '10px' };

    const count = <span title={title}>{children}</span>;

    return (
        <Badge
            badgeContent={count}
            primary={primary}
            secondary={secondary}
            style={style}
            badgeStyle={badgeStyle}
        />
    );
};

export const GroupText = ({ group }) => {
    const appCount = group.applications ? group.applications.length : null;
    const assetCount = group.assets ? group.assets.length : null;

    const name = (group.name) ? group.name : `(${group.id})`;

    return (
        <Link to={`/group/${group.id}`}>
            {name}
            {appCount && <CountBadge title="Applications" primary={true}>{appCount}</CountBadge>}
            {assetCount && <CountBadge title="Assets" secondary={true}>{assetCount}</CountBadge>}
        </Link>
    );
};

export const Group = ({ group, renderGroup, nestedLevel = 0 }) => {
    const nestedItems = group.groups ?
        group.groups.map((item) =>
            <Group key={item.id} group={item} renderGroup={renderGroup} />
        ) : undefined;
    return renderGroup(group, nestedItems, nestedLevel);
};

export const GroupList = ({ groups, renderGroup }) => {
    if (!groups) return <p>No groups</p>;
    return (
        <List>
            {groups.map((group) =>
                <Group key={group.id} group={group} renderGroup={renderGroup} />
            )}
        </List>
    );
};
export default GroupList;
