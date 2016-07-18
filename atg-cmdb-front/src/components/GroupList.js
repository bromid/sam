import React from 'react';
import { Link, withRouter } from 'react-router';
import { connect } from 'react-redux';
import { List, ListItem } from 'material-ui/List';
import Badge from 'material-ui/Badge';
import isArray from 'lodash/isArray';
import isEqual from 'lodash/isEqual';
import * as Actions from '../actions/groupActions';
import LoadingIndicator from './LoadingIndicator';
import { TagFilter } from './Tag';

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

export function Group({ group, nestedLevel = 0 }) {
    const nestedItems = group.groups ?
        group.groups.map(item => <Group group={item} key={item.id} />)
        : undefined;

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
}

function GroupList({ isLoading, groups }) {
    if (isLoading) return <LoadingIndicator />;
    if (!groups) return <p>No results</p>;

    return (
        <List>
            {groups.map(group =>
                <Group group={group} key={group.id} />
            )}
        </List>
    );
}

function Groups({ groups, groupTags = [], addFilter, removeFilter, activeFilter, isLoading }) {
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
}

function toArray(item) {
    return isArray(item) ? item : [item];
}

const GroupsContainer = React.createClass({
    getInitialState() {
        return {};
    },

    componentDidMount() {
        this.fetchGroupList();
    },

    componentWillReceiveProps(nextProps) {
        const { activeFilter } = this.props;

        if (!isEqual(nextProps.activeFilter, activeFilter, { deep: true })) {
            this.fetchGroupList(nextProps);
        }
    },

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

    fetchGroupList(props = this.props) {
        const { activeFilter, fetchGroupList, fetchGroupTags } = props;

        const tags = activeFilter ? { tags: activeFilter.join(',') } : null;
        fetchGroupList(tags);
        fetchGroupTags();
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

function mapStateToProps(state, { location: { query } }) {
    const { groupList, groupListIsPending, groupTags } = state;
    return {
        groups: groupList.items,
        groupTags: groupTags.items,
        activeFilter: query.tags && query.tags.split(','),
        isLoading: groupListIsPending || groupListIsPending === null,
    };
}
export default withRouter(connect(mapStateToProps, Actions)(GroupsContainer));
