import React from 'react';
import { connect } from 'react-redux';
import { Link } from 'react-router';
import { grey100, grey300 } from 'material-ui/styles/colors';
import LoadingIndicator from '../components/LoadingIndicator';
import * as groupActions from '../actions/groupActions';
import { fromGroup } from '../reducers';
import groupBy from 'lodash/groupBy';
import mapValues from 'lodash/mapValues';
import keys from 'lodash/keys';
import flatMap from 'lodash/flatMap';
import sortBy from 'lodash/sortBy';
import sortedUniq from 'lodash/sortedUniq';
import map from 'lodash/map';
import capitalize from 'lodash/capitalize';

const environmentOrder = {
    qa: 1,
    stage: 2,
    internalprod: 3,
    prod: 4,
};

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

const byVersion = (deploymentsMap) => mapValues(deploymentsMap, (list) => groupBy(list, 'version'));

const byEnvironmentAndVersion = (deploymentsList) => {
    const byEnvironment = groupBy(deploymentsList, 'environment');
    return byVersion(byEnvironment);
};

const ServerInfo = ({ servers }) => {
    const label = (servers.length === 1) ? 'server' : 'servers';
    return <span style={{ whiteSpace: 'nowrap' }}>({servers.length} {label})</span>;
};

const ApplicationLabel = ({ styles, application, isLoading }) => (
    <div style={styles.labelCell}>
        <Link to={`/application/${application.id}`}>{application.name}</Link>
        {isLoading &&
            <div style={{ position: 'absolute', display: 'inline' }}>
                <div style={{ position: 'relative', width: 50, height: 30 }}>
                    <LoadingIndicator size={0.4} />
                </div>
            </div>
        }
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
        const { applications, fetchDeployments } = this.props;
        this.fetchDeployments(applications, fetchDeployments);
    },

    componentWillReceiveProps(nextProps) {
        const { groupId, applications, fetchDeployments } = nextProps;
        if (groupId !== this.props.groupId) {
            this.fetchDeployments(applications, fetchDeployments);
        }
    },

    fetchDeployments(applications, fetchDeployments) {
        if (applications) {
            applications.forEach((application) => fetchDeployments(application.id));
        }
    },

    render() {
        const { applications, deployments = {}, deploymentsIsLoading = {} } = this.props;
        if (!applications) return <p>No applications</p>;

        const deploymentsMap = mapValues(deployments, (deploymentsList) =>
            byEnvironmentAndVersion(deploymentsList.items)
        );

        const unsortedEnvironments = flatMap(deploymentsMap,
            (value) => keys(value)
        );

        const environments = sortedUniq(sortBy(unsortedEnvironments, [(value) => {
            const sortField = environmentOrder[value] && `z${environmentOrder[value]}`;
            return sortField || value;
        }]));

        const styles = calculateStyles(environments.length);
        return (
            <div style={{ display: 'flex', flexWrap: 'wrap', marginTop: 40 }}>

                <div style={styles.labelHeaderCell}></div>
                {environments.map((environment) => (
                    <div key={environment} style={styles.headerCell}>
                        <h4>{capitalize(environment)}</h4>
                    </div>
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
                            deployments={deploymentsMap[application.id]}
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

const ApplicationDeployments = ({ group, deployments, deploymentsIsLoading, fetchDeployments }) => {
    if (!group.id) return null;
    return (
        <div>
            <h2>{group.name}</h2>
            <ApplicationDeploymentsList
                groupId={group.id}
                applications={group.applications}
                deployments={deployments}
                deploymentsIsLoading={deploymentsIsLoading}
                fetchDeployments={fetchDeployments}
            />
        </div>
    );
};

const GroupApplicationDeploymentsContainer = (props) => {
    const { groupIsLoading, group, deployments, deploymentsIsLoading, fetchDeployments } = props;
    return (
        <div>
            {groupIsLoading && <LoadingIndicator />}
            {group && <ApplicationDeployments
                group={group}
                deployments={deployments}
                deploymentsIsLoading={deploymentsIsLoading}
                fetchDeployments={fetchDeployments}
            />}
        </div>
    );
};

const mapStateToProps = (state) => ({
    group: fromGroup.getCurrent(state),
    groupIsLoading: fromGroup.getCurrentIsPending(state),
    deployments: fromGroup.getCurrentDeployments(state),
    deploymentsIsLoading: fromGroup.getCurrentDeploymentsIsPending(state),
});

const Actions = {
    fetchDeployments: groupActions.fetchGroupDeployments,
};
export default connect(mapStateToProps, Actions)(GroupApplicationDeploymentsContainer);
