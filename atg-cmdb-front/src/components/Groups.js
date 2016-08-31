import React from 'react';
import { connect } from 'react-redux';
import { Link, withRouter } from 'react-router';
import RaisedButton from 'material-ui/RaisedButton';
import { ListItem } from 'material-ui/List';
import { flexWrapperStyle } from '../style';
import { toArray } from '../helpers';
import LoadingIndicator from './LoadingIndicator';
import { TagFilter } from './Tag';
import { GroupList, GroupText } from './GroupList';
import { fromGroup, getAuthenticated } from '../reducers';

const Groups = (props) => {
    const {
        authenticated, groups, groupTags = [],
        addFilter, removeFilter, activeFilter, isLoading,
    } = props;

    const renderGroup = (group, nestedItems, nestedLevel) => (
        <ListItem
            primaryText={<GroupText group={group} />}
            secondaryText={group.description}
            primaryTogglesNestedList={true}
            nestedItems={nestedItems}
            nestedLevel={nestedLevel}
        />
    );

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
            <GroupList groups={groups} renderGroup={renderGroup} />
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
