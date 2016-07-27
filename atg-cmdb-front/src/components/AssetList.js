import React from 'react';
import { Link } from 'react-router';
import { connect } from 'react-redux';
import { List, ListItem } from 'material-ui/List';
import LoadingIndicator from './LoadingIndicator';

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
    const { assetList, assetListIsPending } = state;
    return {
        assets: assetList.items,
        isLoading: assetListIsPending || assetListIsPending === null,
    };
}
export default connect(mapStateToProps)(AssetListContainer);
