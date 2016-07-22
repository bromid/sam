import React from 'react';
import { Link } from 'react-router';
import { connect } from 'react-redux';
import size from 'lodash/size';
import isEmpty from 'lodash/isEmpty';
import List from 'material-ui/List';
import * as groupActions from '../actions/groupActions';
import * as metaActions from '../actions/metaActions';
import LoadingIndicator from './LoadingIndicator';
import Attributes from './Attributes';
import ItemView from './ItemView';
import { Group as GroupListItem } from './GroupList';

function patchNotification(result, error, isPending) {
    if (isPending) return {};
    if (!isEmpty(error)) {
        return {
            message: 'Failed to update group!',
            duration: 4000,
            action: {
                name: 'info',
            },
        };
    }
    if (!isEmpty(result)) {
        return {
            message: `Updated group ${result.name}`,
        };
    }
    return {};
}

function collectionSize(collection) {
    if (!collection) return ' (0)';
    return ` (${size(collection)})`;
}

function Asset({ asset }) {
    return (
        <p>
            <Link to={`/asset/${asset.id}`}>{asset.name}</Link>
        </p>
    );
}

function Groups({ groups }) {
    if (!groups) return <p>No groups</p>;
    return (
        <List>
            {groups.map(group =>
                <GroupListItem key={group.id} group={group} />
            )}
        </List>
    );
}

function Assets({ assets }) {
    if (!assets) return <p>No assets</p>;
    return (
        <div>
            {assets.map(asset => (
                <Asset key={asset.id} asset={asset} />
            ))}
        </div>
    );
}

function Application({ application }) {
    return (
        <p>
            <Link to={`/application/${application.id}`}>{application.name}</Link>
        </p>
    );
}

function Applications({ applications }) {
    if (!applications) return <p>No applications</p>;
    return (
        <div>
            {applications.map(application => (
                <Application key={application.id} application={application} />
            ))}
        </div>
    );
}

function Group(props) {
    const {
        group: {
            name, description = '', applications, assets,
            tags, attributes, meta, groups,
        },
        notification, updateName, updateDescription,
        metaOpen, toggleMeta, onTagDelete,
    } = props;

    if (!name) return <p>No result</p>;
    const tabs = [
        {
            name: `Applications ${collectionSize(applications)}`,
            node: <Applications applications={applications} />,
        },
        {
            name: `Assets ${collectionSize(assets)}`,
            node: <Assets assets={assets} />,
        },
        {
            name: `Sub groups ${collectionSize(groups)}`,
            node: <Groups groups={groups} />,
        },
        {
            name: `Attributes ${collectionSize(attributes)}`,
            node: <Attributes attributes={attributes} />,
        },
    ];
    return (
        <ItemView
            headline={name}
            updateHeadline={updateName}
            description={description}
            updateDescription={updateDescription}
            tags={tags}
            onTagDelete={onTagDelete}
            meta={meta}
            metaOpen={metaOpen}
            toggleMeta={toggleMeta}
            tabs={tabs}
            notification={notification}
        />
    );
}

const GroupContainer = React.createClass({

    getInitialState() {
        return { initiated: false };
    },

    componentDidMount() {
        const { id, fetchGroup } = this.props;
        fetchGroup(id);
    },

    componentWillReceiveProps(newProps) {
        const { id, patchResult, fetchGroup } = this.props;
        const { id: newId, patchResult: newPatchResult } = newProps;

        const isDifferentEtag = newPatchResult.etag !== patchResult.etag;
        const isUpdatedEtag = !isEmpty(newPatchResult) && isDifferentEtag;
        if (newId !== id || isUpdatedEtag) {
            this.setState({ initiated: true });
            fetchGroup(newId);
        }
    },

    onTagDelete(name) {
        return name;
    },

    updateName(name) {
        const { id, patchGroup, group: { meta } } = this.props;
        patchGroup(id, { name }, {
            hash: meta.hash,
        });
    },

    updateDescription(description) {
        const { id, patchGroup, group: { meta } } = this.props;
        patchGroup(id, { description }, {
            hash: meta.hash,
        });
    },

    render() {
        const {
            group, isLoading,
            metaOpen, toggleMeta,
            patchResult, patchError, patchIsPending,
        } = this.props;

        if (isLoading && !this.state.initiated) return <LoadingIndicator />;
        return (
            <Group
                group={group}
                isLoading={isLoading}
                onTagDelete={this.onTagDelete}
                metaOpen={metaOpen}
                toggleMeta={toggleMeta}
                updateName={this.updateName}
                updateDescription={this.updateDescription}
                notification={() => patchNotification(patchResult, patchError, patchIsPending)}
            />
        );
    },
});

function mapStateToProps(state, props) {
    const {
        metaOpen,
        group, groupError, groupIsPending,
        groupPatchResult, groupPatchResultError, groupPatchResultIsPending,
    } = state;
    const { id } = props.params;
    return {
        id,
        metaOpen,
        group,
        fetchError: groupError,
        patchResult: groupPatchResult,
        patchError: groupPatchResultError,
        patchIsPending: groupPatchResultIsPending,
        isLoading: groupIsPending || groupIsPending === null,
    };
}

const Actions = { ...groupActions, ...metaActions };
export default connect(mapStateToProps, Actions)(GroupContainer);
