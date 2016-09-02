import React from 'react';
import { Link } from 'react-router';
import { List, ListItem } from 'material-ui/List';

const AssetListItem = ({ asset: { id, name, description } }) => (
    <Link to={`/asset/${id}`}>
        <ListItem primaryText={name} secondaryText={description} />
    </Link>
);

export const AssetList = ({ assets }) => {
    if (!assets) return <p>No assets</p>;
    return (
        <List>
            {assets.map((asset) => (
                <AssetListItem key={asset.id} asset={asset} />
            ))}
        </List>
    );
};
