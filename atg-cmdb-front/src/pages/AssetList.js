import React from 'react';
import { Link } from 'react-router';
import { connect } from 'react-redux';
import RaisedButton from 'material-ui/RaisedButton';
import { flexWrapperStyle } from '../style';
import LoadingIndicator from '../components/LoadingIndicator';
import { AssetList } from '../components/AssetList';
import { fromAsset, getAuthenticated } from '../reducers';

const AssetListPage = ({ assets, authenticated }) => (
    <div>
        <div style={{ ...flexWrapperStyle, alignItems: 'center' }}>
            <div style={{ flex: 1 }}>
                <h2>Assets</h2>
            </div>
            {authenticated &&
                <Link to="/asset/new">
                    <RaisedButton
                        label="Add asset"
                        style={{ borderRadius: 3 }}
                    />
                </Link>
            }
        </div>
        <AssetList assets={assets} />
    </div>
);

const AssetListPageContainer = ({ isLoading, authenticated, assets }) => {
    if (isLoading) return <LoadingIndicator />;
    return (
        <AssetListPage
            assets={assets}
            authenticated={authenticated}
        />
    );
};

const mapStateToProps = (state) => ({
    assets: fromAsset.getList(state),
    isLoading: fromAsset.getListIsPending(state),
    authenticated: getAuthenticated(state),
});
export default connect(mapStateToProps)(AssetListPageContainer);
