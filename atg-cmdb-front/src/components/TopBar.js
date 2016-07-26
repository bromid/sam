import React from 'react';
import { connect } from 'react-redux';
import AppBar from 'material-ui/AppBar';
import RaisedButton from 'material-ui/RaisedButton';
import SearchField from './SearchField';
import * as searchActions from '../actions/searchActions';
import * as authActions from '../actions/authActions';
import { flexWrapperStyle } from '../style';

const TopBarContainer = (props) => {
    const {
        authenticated, login, logout, openMenu, mdPlus,
        fetchSearch, searchResults, searchResultsIsPending,
    } = props;

    const onLogout = (event) => {
        event.preventDefault();
        logout();
    };

    const wrapperStyle = {
        ...flexWrapperStyle,
        marginRight: 16,
        marginTop: (authenticated) ? -17 : 0,
    };

    const elementRight = (
        <div style={wrapperStyle}>
            <div style={{ flex: 1, textAlign: 'right' }}>
                {(authenticated) &&
                    <span style={{ color: '#fff', marginBottom: 3, display: 'inline-block' }}>
                        {authenticated.uid} {" "}
                        (<a href="#" onTouchTap={onLogout} style={{ color: '#fff' }}>Sign out</a>)
                    </span>
                }
                <SearchField
                    fetchSearch={fetchSearch}
                    searchResults={searchResults}
                    searchResultsIsPending={searchResultsIsPending}
                />
            </div>
            {(!authenticated) &&
                <RaisedButton
                    style={{ borderRadius: 3, marginLeft: 16 }}
                    labelStyle={{ fontSize: 12 }}
                    onTouchTap={login}
                    label="Sign in"
                />
            }
        </div>
    );

    return (
        <AppBar
            title="Simple Asset Management"
            showMenuIconButton={!mdPlus}
            onLeftIconButtonTouchTap={openMenu}
            style={{ height: 100, padding: 20 }}
            iconElementRight={elementRight}
        />
  );
};

const mapStateToProps = (state) => ({
    authenticated: state.authenticated,
    searchResults: state.searchResults,
    searchResultsIsPending: state.searchResultsIsPending,
});

const Actions = {
    login: authActions.login,
    logout: authActions.logout,
    fetchSearch: searchActions.fetchSearch,
};
export default connect(mapStateToProps, Actions)(TopBarContainer);
