import React from 'react';
import { Link } from 'react-router';
import map from 'lodash/map';
import capitalize from 'lodash/capitalize';
import { grey100, grey300 } from 'material-ui/styles/colors';
import LoadingIndicator from './LoadingIndicator';
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

const ServerInfo = ({ servers }) => {
    const label = (servers.length === 1) ? 'server' : 'servers';
    return <span style={{ whiteSpace: 'nowrap' }}>({servers.length} {label})</span>;
};

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

const ApplicationListDeployments = (props) => {
    const {
        applications = [],
        environments = [],
        deployments = {},
        deploymentsIsLoading = {},
    } = props;

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
};
export default ApplicationListDeployments;
