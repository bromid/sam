import React from 'react';
import { connect } from 'react-redux';
import * as Actions from '../actions/assetActions';
import LoadingIndicator from './LoadingIndicator';
import Attributes from './Attributes';

const AssetContainer = React.createClass({

    componentDidMount() {
        const { id, fetchAsset } = this.props;
        fetchAsset(id);
    },

    render() {
        const { asset, isLoading } = this.props;
        if (isLoading) return <LoadingIndicator />;
        return (
            <div>
                <h2>{asset.name}</h2>
                <p>{asset.description}</p>
                <Attributes attributes={asset.attributes} />
            </div>
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
