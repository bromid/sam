import React from 'react';
import { Link } from 'react-router';
import { connect } from 'react-redux';
import * as assetActions from '../actions/assetActions';
import * as metaActions from '../actions/metaActions';
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
            metaOpen,
            toggleMeta,
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
                metaOpen={metaOpen}
                toggleMeta={toggleMeta}
                tabs={tabs}
            />
        );
    },
});

function mapStateToProps(state, props) {
    const { metaOpen, asset, assetIsLoading } = state;
    const { id } = props.params;
    return {
        id,
        metaOpen,
        asset,
        isLoading: assetIsLoading || assetIsLoading === null,
    };
}

const Actions = { ...assetActions, ...metaActions };
export default connect(mapStateToProps, Actions)(AssetContainer);
