import React from 'react';
import { connect } from 'react-redux';
import getMuiTheme from 'material-ui/styles/getMuiTheme';
import { blue800 } from 'material-ui/styles/colors';
import MuiThemeProvider from 'material-ui/styles/MuiThemeProvider';
import AppBar from 'material-ui/AppBar';
import TextField from 'material-ui/TextField';
import MainMenu from './MainMenu';
import matchMedia from './matchMediaHOC';
import * as menuActions from '../actions/menuActions';

const theme = {
    palette: {
        primary1Color: blue800
    }
};

const App = ({ mdPlus, mainMenuOpen, openMenu, setMenuOpen, children }) => (
    <MuiThemeProvider muiTheme={getMuiTheme(theme)}>
        <div>
            <MainMenu
                isOpen={mainMenuOpen}
                mdPlus={mdPlus}
                setMenuOpen={setMenuOpen}
            />
            <div style={mdPlus ? {marginLeft: 200, position: 'relative', height: '100vh'} : null}>
                <AppBar
                    title="Assets Management Dashboard"
                    showMenuIconButton={!mdPlus}
                    onLeftIconButtonTouchTap={openMenu}
                    style={{height: 100, padding: 20}}
                />
                {children}
            </div>
        </div>
    </MuiThemeProvider>
);

function mapStateToProps(state, { mdPlus }) {
    return {
        mainMenuOpen: state.menuOpen || mdPlus,
    };
}

export default matchMedia(connect(
    mapStateToProps,
    menuActions
)(App));
