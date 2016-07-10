import React from 'react';
import { Link } from 'react-router';
import { connect } from 'react-redux';
import size from 'lodash/size';
import List from 'material-ui/List';
import * as groupActions from '../actions/groupActions';
import * as metaActions from '../actions/metaActions';
import LoadingIndicator from './LoadingIndicator';
import Attributes from './Attributes';
import ItemView from './ItemView';
import { Group as GroupListItem } from './GroupList';

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
        isLoading, name, description, applications, assets, groups, attributes,
        tags, meta, metaOpen, toggleMeta, selectedTab, onTabChanged,
    } = props;

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
            tags={tags}
            meta={meta}
            metaOpen={metaOpen}
            toggleMeta={toggleMeta}
            tabs={tabs}
            selectedTab={selectedTab}
            onTabChanged={onTabChanged}
        />
    );
}

const GroupContainer = React.createClass({

    getInitialState() {
        return {
            selectedTab: 0,
        };
    },

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

    onTabChanged(tab) {
        this.setState({
            selectedTab: tab,
        });
    },

    render() {
        const {
            isLoading,
            metaOpen,
            toggleMeta,
            group: {
                name, description, applications, assets,
                tags, attributes, meta, groups,
            },
        } = this.props;

        return (
            <Group
                isLoading={isLoading}
                name={name}
                description={description}
                applications={applications}
                assets={assets}
                groups={groups}
                attributes={attributes}
                tags={tags}
                meta={meta}
                metaOpen={metaOpen}
                toggleMeta={toggleMeta}
                selectedTab={this.state.selectedTab}
                onTabChanged={this.onTabChanged}
            />
        );
    },
});

function mapStateToProps(state, props) {
    const { metaOpen, group, groupIsLoading } = state;
    const { id } = props.params;
    return {
        id,
        metaOpen,
        group,
        isLoading: groupIsLoading || groupIsLoading === null,
    };
}

const Actions = { ...groupActions, ...metaActions };
export default connect(mapStateToProps, Actions)(GroupContainer);
