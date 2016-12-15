import React from 'react';
import { connect } from 'react-redux';
import { white } from 'material-ui/styles/colors';
import AppBar from 'material-ui/AppBar';
import RaisedButton from 'material-ui/RaisedButton';
import RefreshIndicator from 'material-ui/RefreshIndicator';
import SearchField from './SearchField';
import * as searchActions from '../actions/searchActions';
import * as authActions from '../actions/authActions';
import { flexWrapperStyle } from '../style';
import { fromSearchResults, fromAuth } from '../reducers';

const LoginPendingIcon = () => (
    <RefreshIndicator
        left={24}
        top={0}
        size={38}
        status="loading"
        style={{ boxShadow: 'inherit' }}
    />
);

const SignedInUser = ({ user, onSignOut }) => (
    <span style={{ color: '#fff', marginBottom: 3, display: 'inline-block' }}>
        {user.uid} {" "}
        (<a href="#" onTouchTap={onSignOut} style={{ color: '#fff' }}>Sign out</a>)
    </span>
);

const TopBarContainer = (props) => {
    const {
        authenticatedUser, authenticationIsPending, signInAction, signOutAction, searchAction,
        openMenu, showMenuIcon, searchResults, searchResultsIsLoading,
    } = props;

    const handleSignOut = (event) => {
        event.preventDefault();
        signOutAction();
    };

    const handleSignIn = (event) => {
        event.preventDefault();
        signInAction();
    };

    const wrapperStyle = {
        ...flexWrapperStyle,
        marginRight: 16,
        marginTop: (authenticatedUser) ? -17 : 0,
    };

    const elementRight = (
        <div style={wrapperStyle}>
            <div style={{ flex: 1, textAlign: 'right' }}>
                {(authenticatedUser) &&
                    <SignedInUser user={authenticatedUser} onSignOut={handleSignOut} />
                }
                <SearchField
                    fetchSearch={searchAction}
                    searchResults={searchResults}
                    searchResultsIsLoading={searchResultsIsLoading}
                />
            </div>
            {(!authenticatedUser) &&
                <RaisedButton
                    style={{ borderRadius: 3, marginLeft: 16 }}
                    labelStyle={{ fontSize: 12 }}
                    onTouchTap={handleSignIn}
                    label="Sign in"
                    disabled={authenticationIsPending}
                    disabledBackgroundColor={white}
                    disabledLabelColor={white}
                    icon={(authenticationIsPending) ? <LoginPendingIcon /> : undefined}
                />
            }
        </div>
    );

    return (
        <AppBar
            title="Simple Asset Management"
            showMenuIconButton={showMenuIcon}
            onLeftIconButtonTouchTap={openMenu}
            style={{ height: 100, padding: 20 }}
            iconElementRight={elementRight}
        />
  );
};

const mapStateToProps = (state) => ({
    authenticatedUser: fromAuth.getAuthenticatedUser(state),
    authenticationIsPending: fromAuth.getIsPending(state),
    searchResults: fromSearchResults.getData(state),
    searchResultsIsLoading: fromSearchResults.getIsPending(state),
});

const Actions = {
    signInAction: authActions.signin,
    signOutAction: authActions.signout,
    searchAction: searchActions.fetchSearch,
};
export default connect(mapStateToProps, Actions)(TopBarContainer);
