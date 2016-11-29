import React from 'react';
import { connect } from 'react-redux';
import { Link } from 'react-router';
import { grey100, grey300 } from 'material-ui/styles/colors';
import HomeIcon from 'material-ui/svg-icons/action/home';
import IconButton from 'material-ui/IconButton';
import map from 'lodash/map';
import flatMap from 'lodash/flatMap';
import capitalize from 'lodash/capitalize';
import toLower from 'lodash/toLower';
import isEqual from 'lodash/isEqual';
import intersection from 'lodash/intersection';
import LoadingIndicator from '../components/LoadingIndicator';
import * as groupActions from '../actions/groupActions';
import { fromGroup } from '../reducers';
import { flexWrapperStyle } from '../style';

const calculateStyles = (columns) => {
    const spacing = 50;
    const columnsOrOne = columns || 1;
    const labelCellWidth = (columns === 0) ? 100 : 10;
    const cellWidth = Math.floor(100 * ((100 - labelCellWidth) / columnsOrOne)) / 100;
    const styles = {};
    styles.cell = {
        margin: 0,
        padding: 15,
        background: grey300,
        overflow: 'hidden',
        boxSizing: 'border-box',
        borderLeft: `${spacing}px solid ${grey100}`,
        width: `${cellWidth}%`,
        display: 'flex',
        flexDirection: 'column',
        justifyContent: 'center',
    };
    styles.emptyCell = {
        ...styles.cell,
        background: grey100,
    };
    styles.headerCell = {
        ...styles.cell,
        background: grey100,
        borderLeft: `${spacing}px solid #FFF`,
    };
    styles.labelHeaderCell = {
        background: '#FFF',
        width: `${labelCellWidth}%`,
    };
    styles.labelCell = {
        ...styles.cell,
        background: grey100,
        position: 'relative',
        borderLeft: null,
        paddingRight: 0,
        width: `${labelCellWidth}%`,
    };
    styles.labelSpacer = {
        height: 20,
        width: `${labelCellWidth}%`,
    };
    styles.cellSpacer = {
        height: 20,
        background: grey100,
        boxSizing: 'border-box',
        borderLeft: `${spacing}px solid #FFF`,
        width: `${cellWidth}%`,
    };
    return styles;
};

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

const ServerInfo = ({ servers }) => {
    const label = (servers.length === 1) ? 'server' : 'servers';
    return <span style={{ whiteSpace: 'nowrap' }}>({servers.length} {label})</span>;
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

const EnvironmentLabel = ({ styles, environment }) => (
    <div style={styles.headerCell}>
        <h4>{capitalize(environment)}</h4>
    </div>
);

const ApplicationLabel = ({ styles, application, isLoading }) => (
    <div style={styles.labelCell}>
        <div style={flexWrapperStyle}>
            <Link to={`/application/${application.id}`}>{application.name}</Link>
            {isLoading &&
                <div style={{ position: 'relative', top: 2, width: 25, height: 25, marginLeft: 5 }}>
                    <LoadingIndicator size={20} />
                </div>
            }
        </div>
    </div>
);

const DeploymentVersions = ({ styles, environment, deployments }) => {
    const envDeployments = deployments && deployments[environment];
    const envDeploymentsLabels = envDeployments && map(envDeployments, (servers, version) => (
        <span key={version}>{version} <ServerInfo servers={servers} /><br /></span>
    ));
    return (
        <div style={(envDeploymentsLabels) ? styles.cell : styles.emptyCell}>
            {envDeploymentsLabels}
        </div>
    );
};

const ApplicationDeploymentsList = React.createClass({

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
        const { deployments = {}, deploymentsIsLoading = {}, environments = [] } = this.props;
        const { applications } = this.state;

        if (applications.length === 0) return <p>No applications</p>;

        const styles = calculateStyles(environments.length);
        return (
            <div style={{ ...flexWrapperStyle, marginTop: 40 }}>

                <div style={styles.labelHeaderCell}></div>
                {environments.map((environment) => (
                    <EnvironmentLabel key={environment} styles={styles} environment={environment} />
                ))}

                {applications.map((application) => ([
                    <ApplicationLabel
                        styles={styles}
                        application={application}
                        isLoading={deploymentsIsLoading[application.id]}
                    />,
                    environments.map((environment) => (
                        <DeploymentVersions
                            key={`${application.id}-${environment}`}
                            styles={styles}
                            environment={environment}
                            deployments={deployments[application.id]}
                        />
                    )),
                    <div style={styles.labelSpacer} />,
                    environments.map(() => (
                        <div style={styles.cellSpacer} />
                    )),
                ]))}

            </div>
        );
    },
});

const ApplicationDeployments = (props) => {
    const {
        group, deployments, deploymentsIsLoading, environments, fetchDeployments, filter,
    } = props;

    if (!group.id) return null;
    return (
        <div>
            <Header group={group} />
            <ApplicationDeploymentsList
                group={group}
                deployments={deployments}
                deploymentsIsLoading={deploymentsIsLoading}
                environments={environments}
                fetchDeployments={fetchDeployments}
                filter={filter}
            />
        </div>
    );
};

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
