import React from 'react';
import { connect } from 'react-redux';
import LoadingIndicator from '../components/LoadingIndicator';
import isString from 'lodash/isString';
import { fromReleaseNotes } from '../reducers';

const ReleaseNotesContainer = ({ error, releaseNotesHtml, isLoading }) => {
    if (isLoading) return <LoadingIndicator />;
    if (error) return <p>Failed fetching release notes</p>;

    return (
        <div id={'releaseNotes'} dangerouslySetInnerHTML={releaseNotesHtml} />
    );
};

const mapStateToProps = (state) => {
    const releaseNotes = fromReleaseNotes.getData(state);
    return {
        error: !isString(releaseNotes),
        releaseNotesHtml: { __html: releaseNotes },
        isLoading: fromReleaseNotes.getIsPending(state),
    };
};
export default connect(mapStateToProps)(ReleaseNotesContainer);
