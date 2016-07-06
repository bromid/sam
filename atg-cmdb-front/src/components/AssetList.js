import React from 'react';
import { Link } from 'react-router';
import { connect } from 'react-redux';
import * as Actions from '../actions/assetActions';
import { List, ListItem } from 'material-ui/List';
import LoadingIndicator from './LoadingIndicator';

function Asset({ asset: { id, name, description } }) {
    return (
        <ListItem
            primaryText={
                <Link to={`/asset/${id}`}>
                    {name}
                </Link>
            }
            secondaryText={description}
        />
    );
}

const AssetListContainer = React.createClass({

    componentDidMount() {
        this.props.fetchAssetList();
    },

    render() {
        const { assets, isLoading } = this.props;
        if (isLoading) return <LoadingIndicator />;
        if (!assets) return <p>No results</p>;
        return (
            <List>
                <h2>Assets</h2>
                {assets.map(asset => (
                    <Asset key={asset.id} asset={asset} />
                ))};
            </List>
        );
    },
});

function mapStateToProps(state) {
    const { assetList, assetListIsLoading } = state;
    return {
        assets: assetList.items,
        isLoading: assetListIsLoading || assetListIsLoading === null,
    };
}
export default connect(mapStateToProps, Actions)(AssetListContainer);
