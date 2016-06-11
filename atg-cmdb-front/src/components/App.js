import React from 'react';
import { connect } from 'react-redux';
import Groups from './Groups';
import getMuiTheme from 'material-ui/styles/getMuiTheme';
import { blue400 } from 'material-ui/styles/colors';
import MuiThemeProvider from 'material-ui/styles/MuiThemeProvider';
import AppBar from 'material-ui/AppBar';
import MainMenu from './MainMenu';
import matchMedia from './matchMediaHOC';
import * as menuActions from '../actions/menuActions';

const theme = {
    palette: {
        primary1Color: blue400,
    },
};

const App = ({ mdPlus, mainMenuOpen, openMenu, setMenuOpen, children }) => (
    <MuiThemeProvider muiTheme={getMuiTheme(theme)}>
        <div>
            <MainMenu
                isOpen={mainMenuOpen}
                mdPlus={mdPlus}
                setMenuOpen={setMenuOpen}
            />
            <div style={mdPlus ? { marginLeft: '256px' } : null}>
                <AppBar
                    title="Assets Management Dashboard"
                    showMenuIconButton={!mdPlus}
                    onLeftIconButtonTouchTap={openMenu}
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
