import React from 'react';
import { connect } from 'react-redux';
import * as Actions from '../actions/groupActions';
import { List, ListItem } from 'material-ui/List';
import Subheader from 'material-ui/Subheader';
import Badge from 'material-ui/Badge';
import LoadingIndicator from './LoadingIndicator';

function CountBadge({ children, title, primary, secondary }) {
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
}

function Group({ group, nestedLevel = 0 }) {
    const nestedItems = group.groups ?
        group.groups.map(item => <Group group={item} key={item.id} />)
        : undefined;

    const appCount = group.applications ? group.applications.length : null;
    const assetCount = group.assets ? group.assets.length : null;

    const text = (
        <span>
            {group.name} ({group.id})
            {appCount && <CountBadge title="Applications" primary={true}>{appCount}</CountBadge>}
            {assetCount && <CountBadge title="Assets" secondary={true}>{assetCount}</CountBadge>}
        </span>
    );

    return (
        <ListItem
            primaryText={text}
            secondaryText={group.description}
            primaryTogglesNestedList={true}
            nestedItems={nestedItems}
            nestedLevel={nestedLevel}
            innerDivStyle={{ borderBottom: '1px solid lightgray' }}
        />
    );
}

function Groups({ groups }) {
    return (
        <List>
            <Subheader>Groups</Subheader>
            {groups.map(group => <Group group={group} key={group.id} />)}
        </List>
    );
}

const GroupsContainer = React.createClass({

    componentDidMount() {
        this.props.fetchGroups();
    },

    render() {
        const { isLoading, groups } = this.props;
        if (isLoading) return <LoadingIndicator />;

        return (
            <Groups groups={groups} isLoading={isLoading} />
        );
    },
});

function mapStateToProps(state) {
    const { groups, groupsIsLoading } = state;
    return {
        groups: groups.items,
        isLoading: groupsIsLoading || groupsIsLoading === null,
    };
}
export default connect(mapStateToProps, Actions)(GroupsContainer);
