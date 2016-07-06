import React from 'react';
import { connect } from 'react-redux';
import * as Actions from '../actions/groupActions';
import LoadingIndicator from './LoadingIndicator';
import Attributes from './Attributes';

function Tag({ tag }) {
    return (
        <p>{tag.name}</p>
    );
}

function Tags({ tags }) {
    if (!tags) {
        return (
            <div>
                <h3>Tags</h3>
                <p>No tags</p>
            </div>
        );
    }
    return (
        <div>
            <h3>Tags</h3>
            {tags.map(tag => (
                <Tag tag={tag} />
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
        const { group, isLoading } = this.props;
        if (isLoading) return <LoadingIndicator />;
        return (
            <div style={{ padding: '8px 0' }}>
                <h2>{group.name}</h2>
                <div style={{ margin: 16 }}>
                    <p>{group.description}</p>
                    <Attributes attributes={group.attributes} />
                    <Tags tags={group.tags} />
                </div>
            </div>
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
