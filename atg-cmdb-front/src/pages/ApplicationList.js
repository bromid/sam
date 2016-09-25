import React from 'react';
import { Link } from 'react-router';
import { connect } from 'react-redux';
import RaisedButton from 'material-ui/RaisedButton';
import { flexWrapperStyle } from '../style';
import LoadingIndicator from '../components/LoadingIndicator';
import { ApplicationList } from '../components/ApplicationList';
import { fromApplication, fromAuth } from '../reducers';

const ApplicationListPage = ({ applications, isAuthenticated }) => (
    <div>
        <div style={{ ...flexWrapperStyle, alignItems: 'center' }}>
            <div style={{ flex: 1 }}>
                <h2>Applications</h2>
            </div>
            {isAuthenticated &&
                <Link to="/application/new">
                    <RaisedButton
                        label="Add application"
                        style={{ borderRadius: 3 }}
                    />
                </Link>
            }
        </div>
        <ApplicationList applications={applications} />
    </div>
);

const ApplicationListPageContainer = ({ isLoading, isAuthenticated, applications }) => {
    if (isLoading) return <LoadingIndicator />;
    return (
        <ApplicationListPage
            applications={applications}
            isAuthenticated={isAuthenticated}
        />
    );
};

const mapStateToProps = (state) => ({
    applications: fromApplication.getList(state),
    isLoading: fromApplication.getListIsPending(state),
    isAuthenticated: fromAuth.getIsAuthenticated(state),
});
export default connect(mapStateToProps)(ApplicationListPageContainer);
