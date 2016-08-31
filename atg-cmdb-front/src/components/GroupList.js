import React from 'react';
import { Link, withRouter } from 'react-router';
import { connect } from 'react-redux';
import { List, ListItem } from 'material-ui/List';
import Badge from 'material-ui/Badge';
import RaisedButton from 'material-ui/RaisedButton';
import { flexWrapperStyle } from '../style';
import { toArray } from '../helpers';
import LoadingIndicator from './LoadingIndicator';
import { TagFilter } from './Tag';
import { fromGroup, getAuthenticated } from '../reducers';

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

    const name = (group.name) ? group.name : `(${group.id})`;
    const text = (
        <Link to={`/group/${group.id}`}>
            {name}
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
        />
    );
};

export const GroupList = ({ groups }) => {
    if (!groups) return <p>No groups</p>;
    return (
        <List>
            {groups.map((group) =>
                <Group group={group} key={group.id} />
            )}
        </List>
    );
};

const Groups = (props) => {
    const {
        authenticated, groups, groupTags = [],
        addFilter, removeFilter, activeFilter, isLoading,
    } = props;

    if (isLoading) return <LoadingIndicator />;
    return (
        <div>
            <h2>Groups</h2>
            <div style={{ ...flexWrapperStyle, alignItems: 'baseline' }}>
                <div style={{ flex: 1 }}>
                    <TagFilter
                        dataSource={groupTags}
                        addFilter={addFilter}
                        removeFilter={removeFilter}
                        activeFilter={activeFilter}
                    />
                </div>
                {authenticated &&
                    <Link to="/group/new">
                        <RaisedButton
                            label="Add group"
                            style={{ borderRadius: 3 }}
                        />
                    </Link>
                }
            </div>
            <GroupList groups={groups} />
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
        const { isLoading, authenticated, groups, groupTags, activeFilter } = this.props;

        return (
            <Groups
                isLoading={isLoading}
                authenticated={authenticated}
                groups={groups}
                groupTags={groupTags}
                activeFilter={activeFilter}
                addFilter={this.addFilter}
                removeFilter={this.removeFilter}
            />
        );
    },
});

const mapStateToProps = (state, { location: { query } }) => ({
    groups: fromGroup.getList(state),
    groupTags: fromGroup.getTags(state),
    activeFilter: query.tags && query.tags.split(','),
    isLoading: fromGroup.getListIsPending(state),
    authenticated: getAuthenticated(state),
});
export default withRouter(connect(mapStateToProps)(GroupsContainer));
