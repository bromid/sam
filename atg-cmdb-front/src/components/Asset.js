import React from 'react';
import { Link } from 'react-router';
import { connect } from 'react-redux';
import isEmpty from 'lodash/isEmpty';
import * as assetActions from '../actions/assetActions';
import * as metaActions from '../actions/metaActions';
import LoadingIndicator from './LoadingIndicator';
import Attributes from './Attributes';
import ItemView from './ItemView';

const patchNotification = (result, error, isPending) => {
    if (isPending) return {};
    if (!isEmpty(error)) {
        return {
            message: 'Failed to update asset!',
            duration: 4000,
            action: {
                name: 'info',
            },
        };
    }
    if (!isEmpty(result)) {
        return {
            message: `Updated asset ${result.name}`,
        };
    }
    return {};
};

const AssetContainer = React.createClass({

    updateName(name) {
        const { patchAsset, asset: { id, meta } } = this.props;
        patchAsset(id, { name }, { hash: meta.hash });
    },

    updateDescription(description) {
        const { patchAsset, asset: { id, meta } } = this.props;
        patchAsset(id, { description }, { hash: meta.hash });
    },

    render() {
        const {
            asset, isLoading,
            metaOpen, toggleMeta,
            patchResult, patchError, patchIsPending,
        } = this.props;

        if (isLoading && isEmpty(asset)) return <LoadingIndicator />;

        const { name, description = '', group, attributes, meta } = asset;

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
                updateHeadline={this.updateName}
                description={description}
                updateDescription={this.updateDescription}
                meta={meta}
                metaOpen={metaOpen}
                toggleMeta={toggleMeta}
                tabs={tabs}
                notification={() => patchNotification(patchResult, patchError, patchIsPending)}
                isLoading={isLoading}
            />
        );
    },
});

const mapStateToProps = (state) => {
    const {
        metaOpen,
        asset, assetError, assetIsPending,
        assetPatchResult, assetPatchResultError, assetPatchResultIsPending,
    } = state;
    return {
        metaOpen,
        asset,
        fetchError: assetError,
        patchResult: assetPatchResult,
        patchError: assetPatchResultError,
        patchIsPending: assetPatchResultIsPending,
        isLoading: assetIsPending,
    };
};

const Actions = {
    patchAsset: assetActions.patchAsset,
    toggleMeta: metaActions.toggleMeta,
};
export default connect(mapStateToProps, Actions)(AssetContainer);
