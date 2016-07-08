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
    palette: {
        primary1Color: blue800,
    },
};

function App(props) {
    const {
        mdPlus, mainMenuOpen, openMenu, closeMenu, setMenuOpen,
        children, fetchSearch, searchResults, searchResultsIsLoading,
    } = props;

    const searchField = (
        <SearchField
            fetchSearch={fetchSearch}
            searchResults={searchResults}
            searchResultsIsLoading={searchResultsIsLoading}
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
        searchResultsIsLoading: state.searchResultsIsLoading,
    };
}

export default matchMedia(connect(
    mapStateToProps,
    {
        ...menuActions,
        ...searchActions,
    }
)(App));
