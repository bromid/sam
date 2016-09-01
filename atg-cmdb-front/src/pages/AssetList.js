import React from 'react';
import { connect } from 'react-redux';
import { flexWrapperStyle } from '../style';
import LoadingIndicator from '../components/LoadingIndicator';
import { AssetList } from '../components/AssetList';
import { fromAsset } from '../reducers';

const AssetListPage = ({ assets }) => (
    <div>
        <div style={{ ...flexWrapperStyle, alignItems: 'center' }}>
            <div style={{ flex: 1 }}>
                <h2>Assets</h2>
            </div>
        </div>
        <AssetList assets={assets} />
    </div>
);

const AssetListPageContainer = ({ assets, isLoading }) => {
    if (isLoading) return <LoadingIndicator />;
    return <AssetListPage assets={assets} />;
};

function mapStateToProps(state) {
    return {
        assets: fromAsset.getList(state),
        isLoading: fromAsset.getListIsPending(state),
    };
}
export default connect(mapStateToProps)(AssetListPageContainer);
