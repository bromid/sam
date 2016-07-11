import React from 'react';
import * as Actions from '../actions/infoActions';
import LoadingIndicator from './LoadingIndicator';
import { connect } from 'react-redux';
import isString from 'lodash/isString';

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
    const { releaseNotes, releaseNotesIsLoading } = state;
    return {
        error: !isString(releaseNotes),
        releaseNotesHtml: { __html: releaseNotes },
        isLoading: releaseNotesIsLoading || releaseNotesIsLoading === null,
    };
}

export default connect(mapStateToProps, Actions)(ReleaseNotesContainer);
