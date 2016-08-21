import React, { PropTypes } from 'react';
import { connect } from 'react-redux';
import isFunction from 'lodash/isFunction';
import first from 'lodash/first';
import keys from 'lodash/keys';
import { Tabs, Tab } from 'material-ui/Tabs';
import * as metaActions from '../../actions/metaActions';
import { containerStyle, flexWrapperStyle } from '../../style';
import LoadingIndicator from '../LoadingIndicator';
import Meta from '../Meta';
import { Tags } from '../Tag';
import PersistableDescription from './PersistableDescription';
import PersistableHeadline from './PersistableHeadline';

const mapEditState = (authenticated, editFunction, editing = false, disable = false) => {
    if (disable) return 'readonly';
    if (editing) return 'editing';
    if (authenticated && isFunction(editFunction)) return 'editable';
    return 'readonly';
};

const mapNewEditState = (name, ownState, otherState, value, error) => {
    const stateName = `${name}State`;
    if (otherState === 'saving' || otherState === 'editing' || otherState === 'failed') {
        return {
            [stateName]: 'readonly',
            [name]: value,
        };
    }
    if (ownState === 'failed') {
        return {
            [stateName]: ownState,
            [name]: error.value,
        };
    }
    return {
        [stateName]: ownState,
        [name]: value,
    };
};

const changeEditState = (currentState, authenticated, editFunction, saving, error) => {
    if (currentState === 'saving' || currentState === 'failed') {
        if (error) return 'failed';
    }
    if (currentState === 'editing') {
        if (saving) return 'saving';
        return 'editing';
    }
    return mapEditState(authenticated, editFunction);
};

const parseError = (error) => {
    if (error && error.error) {
        const body = JSON.parse(error.options.body);
        const fieldName = first(keys(body));
        return {
            isError: true,
            message: error.message,
            value: body[fieldName],
        };
    }
    return { isError: false };
};

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
        patchIsPending: PropTypes.bool,
        patchError: PropTypes.object,
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
            headlineState: mapEditState(authenticated, updateHeadline),
            description,
            descriptionErrorText: '',
            descriptionState: mapEditState(authenticated, updateDescription),
        };
    },

    componentWillReceiveProps(newProps) {
        const {
            authenticated, patchIsPending, patchError,
            description, updateDescription,
            headline, updateHeadline,
        } = newProps;

        const {
            headlineState, descriptionState,
        } = this.state;

        const error = parseError(patchError);

        const newHeadlineState = changeEditState(
            headlineState,
            authenticated,
            updateHeadline,
            patchIsPending,
            error.isError
        );

        const newDescState = changeEditState(
            descriptionState,
            authenticated,
            updateDescription,
            patchIsPending,
            error.isError
        );

        this.setState({
            ...mapNewEditState('headline', newHeadlineState, newDescState, headline, error),
            ...mapNewEditState('description', newDescState, newHeadlineState, description, error),
        });
    },

    onTabChanged(tab) {
        this.setState({ selectedTab: tab });
    },

    validateHeadline(value) {
        const validate = this.props.validateHeadline;
        return isFunction(validate) ? validate(value) : '';
    },

    editHeadline(edit = true, disable = null) {
        const { authenticated, updateHeadline } = this.props;
        this.setState({
            headlineState: mapEditState(authenticated, updateHeadline, edit, disable),
        });
        if (disable === null) {
            this.editDescription(false, edit);
        }
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

    editDescription(edit = true, disable = null) {
        const { authenticated, updateDescription } = this.props;
        this.setState({
            descriptionState: mapEditState(authenticated, updateDescription, edit, disable),
        });
        if (disable === null) {
            this.editHeadline(false, edit);
        }
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
            headline, headlineErrorText, headlineState,
            description, descriptionErrorText, descriptionState,
        } = this.state;

        return (
            <div>
                {isLoading && <LoadingIndicator />}
                <PersistableHeadline
                    value={headline}
                    errorText={headlineErrorText}
                    state={headlineState}
                    edit={this.editHeadline}
                    cancel={this.cancelEditHeadline}
                    save={this.saveHeadline}
                    change={this.changeHeadline}
                />
                <Tags tags={tags} onDelete={onTagDelete} />
                <div style={flexWrapperStyle}>
                    <PersistableDescription
                        value={description}
                        errorText={descriptionErrorText}
                        state={descriptionState}
                        edit={this.editDescription}
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
