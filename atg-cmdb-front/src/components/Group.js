import React from 'react';
import { Link } from 'react-router';
import { connect } from 'react-redux';
import size from 'lodash/size';
import List from 'material-ui/List';
import * as Actions from '../actions/groupActions';
import LoadingIndicator from './LoadingIndicator';
import Attributes from './Attributes';
import ItemView from './ItemView';
import { Group } from './GroupList';

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
                <Group key={group.id} group={group} />
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

const GroupContainer = React.createClass({

    componentDidMount() {
        const { id, fetchGroup } = this.props;
        fetchGroup(id);
    },

    componentWillReceiveProps(newProps) {
        const { id, fetchGroup } = this.props;
        const {
            id: newId,
        } = newProps;

        if (newId !== id) {
            fetchGroup(newId);
        }
    },

    render() {
        const {
            isLoading,
            group: {
                name, description, applications, assets,
                tags, attributes, meta, groups,
            },
        } = this.props;
        if (isLoading) return <LoadingIndicator />;
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
                description={description}
                meta={meta}
                tabs={tabs}
                tags={tags}
            />
        );
    },
});

function mapStateToProps(state, props) {
    const { group, groupIsLoading } = state;
    const { id } = props.params;
    return {
        id,
        group,
        isLoading: groupIsLoading || groupIsLoading === null,
    };
}

export default connect(mapStateToProps, Actions)(GroupContainer);
