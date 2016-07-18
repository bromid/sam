import React from 'react';
import { connect } from 'react-redux';
import getMuiTheme from 'material-ui/styles/getMuiTheme';
import { blue800 } from 'material-ui/styles/colors';
import MuiThemeProvider from 'material-ui/styles/MuiThemeProvider';
import AppBar from 'material-ui/AppBar';
import MainMenu from './MainMenu';
import matchMedia from './matchMediaHOC';
import * as menuActions from '../actions/menuActions';
import SearchField from './SearchField';
import * as searchActions from '../actions/searchActions';

const theme = {
    spacing: {
        iconSize: 20,
        desktopGutter: 20,
    },
    palette: {
        primary1Color: blue800,
    },
    tableRow: {
        height: 30,
    },
    tableRowColumn: {
        height: 40,
        spacing: 10,
    },
    tableHeaderColumn: {
        height: 30,
        spacing: 10,
    },
};

function App(props) {
    const {
        mdPlus, mainMenuOpen, openMenu, closeMenu, setMenuOpen,
        children, fetchSearch, searchResults, searchResultsIsPending,
    } = props;

    const searchField = (
        <SearchField
            fetchSearch={fetchSearch}
            searchResults={searchResults}
            searchResultsIsPending={searchResultsIsPending}
        />
    );

    const mdPlusStyle = {
        marginLeft: 200,
        position: 'relative',
        height: '100vh',
    };

    const pageStyle = {
        margin: 20,
    };

    return (
        <MuiThemeProvider muiTheme={getMuiTheme(theme)}>
            <div>
                <MainMenu
                    isOpen={mainMenuOpen}
                    mdPlus={mdPlus}
                    setMenuOpen={setMenuOpen}
                    closeMenu={closeMenu}
                />
                <div style={mdPlus ? mdPlusStyle : null}>
                    <AppBar
                        title="Simple Asset Management"
                        showMenuIconButton={!mdPlus}
                        onLeftIconButtonTouchTap={openMenu}
                        style={{ height: 100, padding: 20 }}
                        iconElementRight={searchField}
                    />
                    <div style={pageStyle}>
                        {children}
                    </div>
                </div>
            </div>
        </MuiThemeProvider>
    );
}

function mapStateToProps(state, { mdPlus }) {
    return {
        mainMenuOpen: state.menuOpen || mdPlus,
        searchResults: state.searchResults,
        searchResultsIsPending: state.searchResultsIsPending,
    };
}

export default matchMedia(connect(
    mapStateToProps,
    {
        ...menuActions,
        ...searchActions,
    }
)(App));
