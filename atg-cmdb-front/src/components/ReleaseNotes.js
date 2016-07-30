import React from 'react';
import * as Actions from '../actions/infoActions';
import LoadingIndicator from './LoadingIndicator';
import { connect } from 'react-redux';
import isString from 'lodash/isString';
import { fromReleaseNotes } from '../reducers';

const ReleaseNotesContainer = React.createClass({

    componentDidMount() {
        this.props.fetchReleaseNotes();
    },

    render() {
        const { error, releaseNotesHtml, isLoading } = this.props;
        if (isLoading) return <LoadingIndicator />;
        if (error) return <p>Failed fetching release notes</p>;
        return (
            <div id={'releaseNotes'} dangerouslySetInnerHTML={releaseNotesHtml} />
        );
    },
});

function mapStateToProps(state) {
    const releaseNotes = fromReleaseNotes.getData(state);
    return {
        error: !isString(releaseNotes),
        releaseNotesHtml: { __html: releaseNotes },
        isLoading: fromReleaseNotes.getIsPending(state),
    };
}

export default connect(mapStateToProps, Actions)(ReleaseNotesContainer);
