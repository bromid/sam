import React from 'react';
import { Link, withRouter } from 'react-router';
import { connect } from 'react-redux';
import { List, ListItem } from 'material-ui/List';
import Badge from 'material-ui/Badge';
import LoadingIndicator from './LoadingIndicator';
import { TagFilter } from './Tag';
import { toArray } from '../helpers';

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

const Group = ({ group, nestedLevel = 0 }) => {
    const nestedItems = group.groups ?
        group.groups.map((item) =>
            <Group group={item} key={item.id} />
        ) : undefined;

    const appCount = group.applications ? group.applications.length : null;
    const assetCount = group.assets ? group.assets.length : null;

    const text = (
        <Link to={`/group/${group.id}`}>
            {group.name} ({group.id})
            {appCount && <CountBadge title="Applications" primary={true}>{appCount}</CountBadge>}
            {assetCount && <CountBadge title="Assets" secondary={true}>{assetCount}</CountBadge>}
        </Link>
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
};

export const GroupList = ({ groups, header }) => {
    if (!groups) return <p>No groups</p>;
    return (
        <List>
            {header}
            {groups.map((group) =>
                <Group group={group} key={group.id} />
            )}
        </List>
    );
};

const Groups = ({ groups, groupTags = [], addFilter, removeFilter, activeFilter, isLoading }) => {
    if (isLoading) return <LoadingIndicator />;
    return (
        <div>
            <h2>Groups</h2>
            <TagFilter
                dataSource={groupTags}
                addFilter={addFilter}
                removeFilter={removeFilter}
                activeFilter={activeFilter}
            />
            <GroupList
                groups={groups}
                isLoading={isLoading}
            />
        </div>
    );
};

const GroupsContainer = React.createClass({

    setFilter(filterQuery) {
        const { router, location } = this.props;

        router.push({
            query: filterQuery,
            pathname: location.pathname,
        });
    },

    addFilter(newFilter) {
        const { activeFilter = [] } = this.props;

        const updatedFilter = toArray(activeFilter)
            .concat(newFilter.name)
            .join(',');

        this.setFilter({ tags: updatedFilter });
    },

    removeFilter(filter) {
        const { activeFilter = [] } = this.props;
        const updatedFilter = activeFilter.filter((item) => item !== filter);

        this.setFilter(updatedFilter.length ? { tags: updatedFilter } : null);
    },

    render() {
        const { isLoading, groups, groupTags, activeFilter } = this.props;

        return (
            <Groups
                isLoading={isLoading}
                groups={groups}
                groupTags={groupTags}
                activeFilter={activeFilter}
                addFilter={this.addFilter}
                removeFilter={this.removeFilter}
            />
        );
    },
});

const mapStateToProps = (state, { location: { query } }) => {
    const { groupList, groupListIsPending, groupTags } = state;
    return {
        groups: groupList.items,
        groupTags: groupTags.items,
        activeFilter: query.tags && query.tags.split(','),
        isLoading: groupListIsPending || groupListIsPending === null,
    };
};
export default withRouter(connect(mapStateToProps)(GroupsContainer));
