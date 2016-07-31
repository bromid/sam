import React from 'react';
import { Link } from 'react-router';
import { connect } from 'react-redux';
import { List, ListItem } from 'material-ui/List';
import LoadingIndicator from './LoadingIndicator';
import { fromAsset } from '../reducers';

const Asset = ({ asset: { id, name, description } }) => (
    <Link to={`/asset/${id}`}>
        <ListItem primaryText={name} secondaryText={description} />
    </Link>
);

export const AssetList = ({ assets, header }) => {
    if (!assets) return <p>No assets</p>;
    return (
        <List>
            {header}
            {assets.map((asset) => (
                <Asset key={asset.id} asset={asset} />
            ))}
        </List>
    );
};

const AssetListContainer = ({ assets, isLoading }) => {
    if (isLoading) return <LoadingIndicator />;
    return <AssetList assets={assets} header={<h2>Assets</h2>} />;
};

function mapStateToProps(state) {
    return {
        assets: fromAsset.getList(state),
        isLoading: fromAsset.getListIsPending(state),
    };
}
export default connect(mapStateToProps)(AssetListContainer);
