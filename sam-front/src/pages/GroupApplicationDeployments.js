import React from 'react';
import { Link } from 'react-router';
import { connect } from 'react-redux';
import HomeIcon from 'material-ui/svg-icons/action/home';
import IconButton from 'material-ui/IconButton';
import flatMap from 'lodash/flatMap';
import toLower from 'lodash/toLower';
import isEqual from 'lodash/isEqual';
import intersection from 'lodash/intersection';
import ApplicationListDeployments from '../components/ApplicationListDeployments';
import LoadingIndicator from '../components/LoadingIndicator';
import * as groupActions from '../actions/groupActions';
import { fromGroup } from '../reducers';

const applicationsFromGroupAndSubgroups = (group) => {
    if (group.groups) {
        return flatMap(group.groups, (subGroup) => applicationsFromGroupAndSubgroups(subGroup));
    }
    return group.applications || [];
};

const applicationsFromGroup = (group, filter) => {
    if (filter.includeSubgroups) {
        return applicationsFromGroupAndSubgroups(group);
    }
    return group.applications || [];
};

const Header = ({ group }) => (
    <div style={{ position: 'relative', width: '100%' }}>
        <h2>{group.name}</h2>
        <Link to={`/group/${group.id}`}>
            <IconButton style={{ position: 'absolute', right: -9, top: -9 }} tooltip="Group page">
                <HomeIcon />
            </IconButton>
        </Link>
    </div>
);

const Filter = () => (
    <div style={{ marginTop: 20 }}></div>
);

const ApplicationDeployments = React.createClass({

    componentWillMount() {
        const { group, filter, fetchDeployments } = this.props;
        this.fetchDeployments(group, filter, fetchDeployments);
    },

    componentWillReceiveProps(nextProps) {
        const { group, filter, fetchDeployments } = nextProps;
        if (group.id !== this.props.group.id || !isEqual(filter, this.props.filter)) {
            this.fetchDeployments(group, filter, fetchDeployments);
        }
    },

    fetchDeployments(group, filter, fetchDeployments) {
        const applications = applicationsFromGroup(group, filter);
        applications.forEach((application) => fetchDeployments(application.id));
        this.setState({ applications });
    },

    render() {
        const { group, deployments, deploymentsIsLoading, environments, filter } = this.props;
        const { applications } = this.state;

        if (!group.id) return <p>No group found</p>;
        return (
            <div>
                <Header group={group} />
                <Filter filter={filter} />
                <ApplicationListDeployments
                    applications={applications}
                    deployments={deployments}
                    deploymentsIsLoading={deploymentsIsLoading}
                    environments={environments}
                />
            </div>
        );
    },
});

const GroupApplicationDeploymentsContainer = (props) => {
    const {
        groupIsLoading, group, deployments, deploymentsIsLoading, environments, fetchDeployments,
        includeSubgroups, environmentsFilter,
    } = props;

    const filter = {
        includeSubgroups,
        environments: environmentsFilter || [],
    };

    const filteredEnvironments = environmentsFilter ?
        intersection(environments, environmentsFilter) : environments;

    if (groupIsLoading) return <LoadingIndicator />;
    return (
        <ApplicationDeployments
            group={group}
            deployments={deployments}
            deploymentsIsLoading={deploymentsIsLoading}
            environments={filteredEnvironments}
            fetchDeployments={fetchDeployments}
            filter={filter}
        />
    );
};

const mapStateToProps = (state, { location: { query } }) => ({
    environmentsFilter: query.environments && query.environments.split(','),
    includeSubgroups: query.includeSubgroups && toLower(query.includeSubgroups) === 'true',
    group: fromGroup.getCurrent(state),
    groupIsLoading: fromGroup.getCurrentIsPending(state),
    deployments: fromGroup.getCurrentDeploymentsByEnvironmentAndVersion(state),
    environments: fromGroup.getCurrentDeploymentsEnvironments(state),
    deploymentsIsLoading: fromGroup.getCurrentDeploymentsIsPending(state),
});

const Actions = {
    fetchDeployments: groupActions.fetchGroupDeployments,
};
export default connect(mapStateToProps, Actions)(GroupApplicationDeploymentsContainer);
