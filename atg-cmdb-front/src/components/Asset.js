import React from 'react';
import { connect } from 'react-redux';
import * as Actions from '../actions/assetActions';
import LoadingIndicator from './LoadingIndicator';
import Attributes from './Attributes';
import ItemView from './ItemView';

const AssetContainer = React.createClass({

    componentDidMount() {
        const { id, fetchAsset } = this.props;
        fetchAsset(id);
    },

    render() {
        const { asset, isLoading } = this.props;
        if (isLoading) return <LoadingIndicator />;

        const tabs = [
            {
                name: 'Attributes',
                node: <Attributes attributes={asset.attributes} />,
            },
        ];
        return (
            <ItemView
                headline={asset.name}
                description={asset.description}
                meta={asset.meta}
                tabs={tabs}
            />
        );
    },
});

function mapStateToProps(state, props) {
    const { asset, assetIsLoading } = state;
    const { params } = props;
    return {
        asset,
        id: params.id,
        isLoading: assetIsLoading || assetIsLoading === null,
    };
}
export default connect(mapStateToProps, Actions)(AssetContainer);
