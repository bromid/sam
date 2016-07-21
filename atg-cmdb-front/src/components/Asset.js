import React from 'react';
import { Link } from 'react-router';
import { connect } from 'react-redux';
import isEmpty from 'lodash/isEmpty';
import * as assetActions from '../actions/assetActions';
import * as metaActions from '../actions/metaActions';
import LoadingIndicator from './LoadingIndicator';
import Attributes from './Attributes';
import ItemView from './ItemView';

function patchNotification(result, error, isPending) {
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
}

const AssetContainer = React.createClass({

    getInitialState() {
        return { initiated: false };
    },

    componentDidMount() {
        const { id, fetchAsset } = this.props;
        fetchAsset(id);
    },

    componentWillReceiveProps(newProps) {
        const { id, patchResult, fetchAsset } = this.props;
        const { id: newId, patchResult: newPatchResult } = newProps;

        const isDifferentEtag = newPatchResult.etag !== patchResult.etag;
        const isUpdatedEtag = !isEmpty(newPatchResult) && isDifferentEtag;
        if (newId !== id || isUpdatedEtag) {
            this.setState({ initiated: true });
            fetchAsset(newId);
        }
    },

    updateName(name) {
        const { id, patchAsset, asset: { meta } } = this.props;
        patchAsset(id, { name }, {
            hash: meta.hash,
        });
    },

    updateDescription(description) {
        const { id, patchAsset, asset: { meta } } = this.props;
        patchAsset(id, { description }, {
            hash: meta.hash,
        });
    },

    render() {
        const {
            isLoading,
            metaOpen, toggleMeta,
            patchResult, patchError, patchIsPending,
            asset: {
                name, description = '', group, attributes, meta,
            },
        } = this.props;
        if (isLoading && !this.state.initiated) return <LoadingIndicator />;

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
            />
        );
    },
});

function mapStateToProps(state, props) {
    const {
        metaOpen,
        asset, assetError, assetIsPending,
        assetPatchResult, assetPatchResultError, assetPatchResultIsPending,
    } = state;
    const { id } = props.params;
    return {
        id,
        metaOpen,
        asset,
        fetchError: assetError,
        patchResult: assetPatchResult,
        patchError: assetPatchResultError,
        patchIsPending: assetPatchResultIsPending,
        isLoading: assetIsPending || assetIsPending === null,
    };
}

const Actions = { ...assetActions, ...metaActions };
export default connect(mapStateToProps, Actions)(AssetContainer);
