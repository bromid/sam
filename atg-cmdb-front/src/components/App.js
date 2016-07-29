import React from 'react';
import { connect } from 'react-redux';
import { blue800 } from 'material-ui/styles/colors';
import getMuiTheme from 'material-ui/styles/getMuiTheme';
import MuiThemeProvider from 'material-ui/styles/MuiThemeProvider';
import MainMenu from './MainMenu';
import matchMedia from './matchMediaHOC';
import * as menuActions from '../actions/menuActions';
import { getIsMenuOpen } from '../reducers';
import TopBar from './TopBar';
import Notifier from './Notifier';

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
        mdPlus, notification, children,
        mainMenuOpen, openMenu, closeMenu, setMenuOpen,
    } = props;

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
                    <TopBar mdPlus={mdPlus} openMenu={openMenu} />
                    <div style={pageStyle}>
                        {children}
                    </div>
                </div>
                <Notifier notification={notification} />
            </div>
        </MuiThemeProvider>
    );
}

const mapStateToProps = (state, { mdPlus }) => ({
    mainMenuOpen: getIsMenuOpen(state) || mdPlus,
});

const Actions = {
    setMenuOpen: menuActions.setMenuOpen,
    openMenu: menuActions.openMenu,
    closeMenu: menuActions.closeMenu,
};
export default matchMedia(connect(mapStateToProps, Actions)(App));
