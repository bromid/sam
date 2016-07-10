import React, { PropTypes } from 'react';
import { Link } from 'react-router';
import Dialog from 'material-ui/Dialog';
import RaisedButton from 'material-ui/RaisedButton';
import LoadingIndicator from './LoadingIndicator';
import {
    Table, TableBody, TableHeader, TableHeaderColumn,
    TableRow, TableRowColumn,
} from 'material-ui/Table';
import isEmpty from 'lodash/isEmpty';

const SearchResultDialog = React.createClass({
    propTypes: {
        searchResults: PropTypes.object,
        modalOpen: PropTypes.bool,
        searchResultsIsLoading: PropTypes.bool,
    },

    renderServersTable() {
        const { searchResults: { servers }, handleCloseModal } = this.props;

        if (isEmpty(servers.items)) {
            return (
                <div>
                    <h3>Servers</h3>
                    <p>No results</p>
                </div>
            );
        }

        const serverTableRows = servers && servers.items.map((server, index) =>
            <TableRow key={`tableRow-${index}`}>
                <TableRowColumn>
                    <Link
                        onClick={handleCloseModal}
                        to={`/server/${server.environment}/${server.hostname}`}
                    >
                        {server.hostname}@{server.environment}
                    </Link>
                </TableRowColumn>
                <TableRowColumn>{server.fqdn}</TableRowColumn>
                <TableRowColumn>{server.description}</TableRowColumn>
            </TableRow>
            );

        return (
            <div>
                <h3>Servers</h3>
                <Table>
                    <TableHeader displaySelectAll={false} adjustForCheckbox={false}>
                        <TableRow>
                            <TableHeaderColumn>Host</TableHeaderColumn>
                            <TableHeaderColumn>FQDN</TableHeaderColumn>
                            <TableHeaderColumn>Description</TableHeaderColumn>
                        </TableRow>
                    </TableHeader>
                    <TableBody displayRowCheckbox={false}>
                               {serverTableRows}
                    </TableBody>
                </Table>
            </div>
        );
    },

    render() {
        const { searchResults, searchResultsIsLoading, modalOpen, handleCloseModal } = this.props;

        const isLoading = searchResultsIsLoading || _.isEmpty(searchResults);

        const actions = [
            <RaisedButton
                label="Close"
                secondary={true}
                onTouchTap={handleCloseModal}
            />,
        ];

        return (
            <Dialog
                title="Search Results"
                open={modalOpen}
                actions={actions}
                autoScrollBodyContent={true}
            >
                <div style={{ minHeight: 500, marginTop: 20 }}>
                     {isLoading && <LoadingIndicator />}
                     {!isLoading && this.renderServersTable()}
                </div>
            </Dialog>
        );
    },
});

export default SearchResultDialog;
