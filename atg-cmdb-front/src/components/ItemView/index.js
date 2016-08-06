import React, { PropTypes } from 'react';
import { connect } from 'react-redux';
import isFunction from 'lodash/isFunction';
import { Tabs, Tab } from 'material-ui/Tabs';
import * as metaActions from '../../actions/metaActions';
import { containerStyle, flexWrapperStyle } from '../../style';
import LoadingIndicator from '../LoadingIndicator';
import Meta from '../Meta';
import { Tags } from '../Tag';
import PersistableDescription from './PersistableDescription';
import PersistableHeadline from './PersistableHeadline';

const ItemViewContainer = React.createClass({
    propTypes: {
        headline: PropTypes.string.isRequired,
        updateHeadline: PropTypes.func,
        validateHeadline: PropTypes.func,
        description: PropTypes.string.isRequired,
        updateDescription: PropTypes.func,
        validateDescription: PropTypes.func,
        meta: PropTypes.object,
        tabs: PropTypes.array,
        tags: PropTypes.array,
        onTagDelete: PropTypes.func,
        isLoading: PropTypes.bool,
    },

    getInitialState() {
        const {
            authenticated,
            description, updateDescription,
            headline, updateHeadline,
        } = this.props;

        return {
            selectedTab: 0,
            headline,
            headlineErrorText: '',
            headlineEditable: authenticated && isFunction(updateHeadline),
            headlineEditActive: false,
            description,
            descriptionErrorText: '',
            descriptionEditable: authenticated && isFunction(updateDescription),
            descriptionEditActive: false,
        };
    },

    componentWillReceiveProps(newProps) {
        const {
            authenticated,
            description, updateDescription,
            headline, updateHeadline,
        } = newProps;

        this.setState({
            headline,
            headlineEditable: authenticated && isFunction(updateHeadline),
            description,
            descriptionEditable: authenticated && isFunction(updateDescription),
        });
    },

    onTabChanged(tab) {
        this.setState({ selectedTab: tab });
    },

    validateHeadline(value) {
        const validate = this.props.validateHeadline;
        return isFunction(validate) ? validate(value) : '';
    },

    editHeadline(edit = true) {
        this.setState({ headlineEditActive: edit });
    },

    cancelEditHeadline() {
        this.setState({ headline: this.props.headline, headlineErrorText: '' });
        this.editHeadline(false);
    },

    changeHeadline(event) {
        const headline = event.target.value;
        const headlineErrorText = this.validateHeadline(headline);
        this.setState({ headline, headlineErrorText });
    },

    saveHeadline(event) {
        event.preventDefault();
        const { headline, headlineErrorText } = this.state;
        if (headlineErrorText.length < 1) {
            this.editHeadline(false);
            this.props.updateHeadline(headline);
        }
    },

    validateDescription(value) {
        const validate = this.props.validateDescription;
        return isFunction(validate) ? validate(value) : '';
    },

    editDescription(edit = true) {
        this.setState({ descriptionEditActive: edit });
    },

    cancelEditDescription() {
        this.setState({ description: this.props.description, descriptionErrorText: '' });
        this.editDescription(false);
    },

    changeDescription(event) {
        const description = event.target.value;
        const descriptionErrorText = this.validateDescription(description);
        this.setState({ description, descriptionErrorText });
    },

    saveDescription(event) {
        event.preventDefault();
        const { description, descriptionErrorText } = this.state;
        if (descriptionErrorText.length < 1) {
            this.editDescription(false);
            this.props.updateDescription(description);
        }
    },

    render() {
        const {
            isLoading, tabs,
            tags, onTagDelete,
            meta, metaOpen, toggleMeta,
        } = this.props;

        const {
            headline, headlineErrorText, headlineEditable, headlineEditActive,
            description, descriptionErrorText, descriptionEditable, descriptionEditActive,
        } = this.state;

        return (
            <div>
                {isLoading && <LoadingIndicator />}
                <PersistableHeadline
                    value={headline}
                    errorText={headlineErrorText}
                    editActive={headlineEditActive}
                    edit={(headlineEditable) ? this.editHeadline : null}
                    cancel={this.cancelEditHeadline}
                    save={this.saveHeadline}
                    change={this.changeHeadline}
                />
                <Tags tags={tags} onDelete={onTagDelete} />
                <div style={flexWrapperStyle}>
                    <PersistableDescription
                        value={description}
                        errorText={descriptionErrorText}
                        editActive={descriptionEditActive}
                        edit={(descriptionEditable) ? this.editDescription : null}
                        cancel={this.cancelEditDescription}
                        save={this.saveDescription}
                        change={this.changeDescription}
                    />
                    <Meta meta={meta} open={metaOpen} toggle={toggleMeta} />
                </div>
                <Tabs value={this.state.selectedTab} onChange={this.onTabChanged}>
                    {tabs.map((tab, index) => (
                        <Tab key={tab.name} value={index} label={tab.name}>
                            <div style={containerStyle}>
                                {tab.node}
                            </div>
                        </Tab>
                    ))}
                </Tabs>
            </div>
        );
    },
});

const mapStateToProps = (state) => {
    const { metaOpen, authenticated } = state;
    return { metaOpen, authenticated };
};
const Actions = {
    toggleMeta: metaActions.toggleMeta,
};
export default connect(mapStateToProps, Actions)(ItemViewContainer);
