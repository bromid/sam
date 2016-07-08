import React from 'react';
import { Link } from 'react-router';
import { connect } from 'react-redux';
import size from 'lodash/size';
import * as Actions from '../actions/groupActions';
import LoadingIndicator from './LoadingIndicator';
import Attributes from './Attributes';
import ItemView from './ItemView';

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

    render() {
        const {
            isLoading,
            group: {
                name, description, applications, assets, tags, attributes, meta,
            },
        } = this.props;
        if (isLoading) return <LoadingIndicator />;
        if (!name) return <p>No result</p>;

        const subgroups = [];
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
                name: `Sub groups ${collectionSize(subgroups)}`,
                node: <div />,
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
