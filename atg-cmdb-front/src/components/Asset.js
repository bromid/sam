import React from 'react';
import { Link } from 'react-router';
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
        const {
            isLoading,
            asset: {
                name, description, group, attributes, meta,
            },
        } = this.props;
        if (isLoading) return <LoadingIndicator />;

        const tabs = [
            {
                name: 'Details',
                node: (
                    <div>
                        <dl>
                            <dt>Group</dt>
                            <dd>{group && <Link to={`/group/${group.id}`}>{group.name}</Link>}</dd>
                        </dl>
                        <Attributes attributes={attributes} />
                    </div>
                ),
            },
        ];
        return (
            <ItemView
                headline={name}
                description={description}
                meta={meta}
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
