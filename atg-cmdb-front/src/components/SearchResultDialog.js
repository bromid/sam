import React from 'react';
import { Link } from 'react-router';
import Dialog from 'material-ui/Dialog';
import AppBar from 'material-ui/AppBar';
import RaisedButton from 'material-ui/RaisedButton';
import LoadingIndicator from './LoadingIndicator';
import {
    Table, TableBody, TableHeader, TableHeaderColumn,
    TableRow, TableRowColumn,
} from 'material-ui/Table';
import isEmpty from 'lodash/isEmpty';
import { container } from '../style';
import { Tags } from './Tag';

function ResultsTable({ header, tableHeaders, rows }) {
    if (isEmpty(rows)) {
        return (
            <div style={{ marginBottom: 20 }}>
                <h3>{header}</h3>
                <div style={{ ...container, fontSize: 13 }}>
                    <p>No results</p>
                </div>
            </div>
        );
    }
    return (
        <div style={{ marginBottom: 20 }}>
            <h3>{header}</h3>
            <div style={container}>
                <Table selectable={false}>
                    <TableHeader displaySelectAll={false} adjustForCheckbox={false}>
                        <TableRow>
                            {tableHeaders.map((tableHeader, index) =>
                                <TableHeaderColumn key={`headerColumn-${index}`}>
                                    {tableHeader}
                                </TableHeaderColumn>
                            )}
                        </TableRow>
                    </TableHeader>
                    <TableBody displayRowCheckbox={false}>
                        {rows.map((row, rowIndex) =>
                            <TableRow key={`tableRow-${rowIndex}`}>
                                {row.map((col, colIndex) =>
                                    <TableRowColumn key={`tableColumn-${colIndex}`}>
                                        {col}
                                    </TableRowColumn>
                                )}
                            </TableRow>
                        )}
                    </TableBody>
                </Table>
            </div>
        </div>
    );
}

function GroupsTable({ groups = {}, handleCloseModal }) {
    const tableHeaders = ['Name', 'Description', 'Tags'];
    const rows = groups.items && groups.items.map((group) => [
        <Link onClick={handleCloseModal} to={`/group/${group.id}`}>
            {group.name}
        </Link>,
        group.description,
        <Tags tags={group.tags} />,
    ]);
    return <ResultsTable header="Groups" tableHeaders={tableHeaders} rows={rows} />;
}

function ApplicationsTable({ applications = {}, handleCloseModal }) {
    const tableHeaders = ['Name', 'Description'];
    const rows = applications.items && applications.items.map((application) => [
        <Link onClick={handleCloseModal} to={`/application/${application.id}`}>
            {application.name}
        </Link>,
        application.description,
    ]);
    return <ResultsTable header="Applications" tableHeaders={tableHeaders} rows={rows} />;
}

function ServersTable({ servers = {}, handleCloseModal }) {
    const tableHeaders = ['Host', 'FQDN', 'Description'];
    const rows = servers.items && servers.items.map((server) => [
        <Link
            onClick={handleCloseModal}
            to={`/server/${server.environment}/${server.hostname}`}
        >
            {server.hostname}@{server.environment}
        </Link>,
        server.fqdn,
        server.description,
    ]);
    return <ResultsTable header="Servers" tableHeaders={tableHeaders} rows={rows} />;
}

function AssetsTable({ assets = {}, handleCloseModal }) {
    const tableHeaders = ['Name', 'Description'];
    const rows = assets.items && assets.items.map((asset) => [
        <Link onClick={handleCloseModal} to={`/asset/${asset.id}`}>
            {asset.name}
        </Link>,
        asset.description,
    ]);
    return <ResultsTable header="Assets" tableHeaders={tableHeaders} rows={rows} />;
}

function SearchResults({ searchResults = {}, isLoading, handleCloseModal }) {
    const { groups, applications, servers, assets } = searchResults;
    if (isLoading) return <LoadingIndicator />;
    return (
        <div>
            <GroupsTable groups={groups} handleCloseModal={handleCloseModal} />
            <ApplicationsTable applications={applications} handleCloseModal={handleCloseModal} />
            <ServersTable servers={servers} handleCloseModal={handleCloseModal} />
            <AssetsTable assets={assets} handleCloseModal={handleCloseModal} />
        </div>
    );
}

export default function SearchResultDialog(props) {
    const { isLoading, searchResults, modalOpen, handleCloseModal } = props;

    const actions = [
        <RaisedButton
            label="Close"
            secondary={true}
            onTouchTap={handleCloseModal}
        />,
    ];

    const titleBar = (
        <AppBar
            title="SEARCH RESULT"
            style={{
                borderRadius: 2,
                padding: '0 24px 0 16px',
            }}
            titleStyle={{
                fontSize: 14,
                fontWeight: 500,
                height: 48,
                lineHeight: '48px',
            }}
            showMenuIconButton={false}
        />
    );

    return (
        <Dialog
            title={titleBar}
            open={modalOpen}
            actions={actions}
            onRequestClose={handleCloseModal}
            autoScrollBodyContent={true}
        >
            <div style={{ minHeight: 500, marginTop: 20 }}>
                <SearchResults
                    isLoading={isLoading}
                    handleCloseModal={handleCloseModal}
                    searchResults={searchResults}
                />
            </div>
        </Dialog>
    );
}
