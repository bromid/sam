import React from 'react';
import { Link } from 'react-router';
import { connect } from 'react-redux';
import isEmpty from 'lodash/isEmpty';
import * as assetActions from '../actions/assetActions';
import LoadingIndicator from './LoadingIndicator';
import Attributes from './Attributes';
import ItemView from './ItemView';
import { fromAsset } from '../reducers';

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
        const { asset, isLoading } = this.props;
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
                tabs={tabs}
                isLoading={isLoading}
            />
        );
    },
});

const mapStateToProps = (state) => ({
    asset: fromAsset.getCurrent(state),
    fetchError: fromAsset.getCurrentError(state),
    patchResult: fromAsset.getPatchResult(state),
    patchError: fromAsset.getPatchResultError(state),
    patchIsPending: fromAsset.getPatchResultIsPending(state),
    isLoading: fromAsset.getCurrentIsPending(state),
});

const Actions = {
    patchAsset: assetActions.patchAsset,
};
export default connect(mapStateToProps, Actions)(AssetContainer);
