import React from 'react';
import { connect } from 'react-redux';
import { blue800 } from 'material-ui/styles/colors';
import getMuiTheme from 'material-ui/styles/getMuiTheme';
import MuiThemeProvider from 'material-ui/styles/MuiThemeProvider';
import MainMenu from '../components/MainMenu';
import Signin from '../components/Signin';
import matchMedia from '../components/matchMediaHOC';
import * as menuActions from '../actions/menuActions';
import { getNotification, fromMenu } from '../reducers';
import TopBar from '../components/TopBar';
import Notifier from '../components/Notifier';

const theme = {
    fontFamily: 'Roboto, Helvetica Neue, Helvetica, sans-serif',
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
        dashboardMode, mdPlus, notification, children,
        mainMenuOpen, openMenu, closeMenu, setMenuOpen,
    } = props;

    const dockedMainMenuStyle = {
        marginLeft: 200,
        position: 'relative',
        height: '100vh',
    };

    const pageStyle = {
        margin: 20,
    };

    const mainMenuDocked = mdPlus && !dashboardMode;

    return (
        <MuiThemeProvider muiTheme={getMuiTheme(theme)}>
            <div>
                <MainMenu
                    isOpen={mainMenuOpen || mainMenuDocked}
                    docked={mainMenuDocked}
                    setMenuOpen={setMenuOpen}
                    closeMenu={closeMenu}
                />
                <div style={mainMenuDocked ? dockedMainMenuStyle : null}>
                    <TopBar showMenuIcon={!mainMenuDocked} openMenu={openMenu} />
                    <div style={pageStyle}>
                        {children}
                    </div>
                </div>
                <Notifier notification={notification} />
                <Signin />
            </div>
        </MuiThemeProvider>
    );
}

const mapStateToProps = (state) => ({
    dashboardMode: fromMenu.getIsDashboardMode(state),
    mainMenuOpen: fromMenu.getIsOpen(state),
    notification: getNotification(state),
});

const Actions = {
    setMenuOpen: menuActions.setMenuOpen,
    openMenu: menuActions.openMenu,
    closeMenu: menuActions.closeMenu,
};
export default matchMedia(connect(mapStateToProps, Actions)(App));
