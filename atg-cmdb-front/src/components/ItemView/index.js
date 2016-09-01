import React, { PropTypes } from 'react';
import { connect } from 'react-redux';
import isFunction from 'lodash/isFunction';
import isNumber from 'lodash/isNumber';
import { Tabs, Tab } from 'material-ui/Tabs';
import * as metaActions from '../../actions/metaActions';
import { containerStyle, flexWrapperStyle } from '../../style';
import LoadingIndicator from '../LoadingIndicator';
import Meta from '../Meta';
import { Tags } from '../Tag';
import PersistableDescription from './PersistableDescription';
import PersistableHeadline from './PersistableHeadline';
import { State } from '../EditState';
import * as StateMachine from '../EditStateMachine';

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
            headlineState: StateMachine.mapState(authenticated, updateHeadline),
            description,
            descriptionErrorText: '',
            descriptionState: StateMachine.mapState(authenticated, updateDescription),
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

        const error = StateMachine.parseError(patchError);

        const newHeadlineState = StateMachine.mapStateFromCurrent(
            headlineState,
            authenticated,
            updateHeadline,
            patchIsPending,
            error.isError
        );

        const newDescState = StateMachine.mapStateFromCurrent(
            descriptionState,
            authenticated,
            updateDescription,
            patchIsPending,
            error.isError
        );

        this.setState({
            ...StateMachine.newPropsState(
                'headline',
                newHeadlineState,
                newDescState,
                headline,
                error
            ),
            ...StateMachine.newPropsState(
                'description',
                newDescState,
                newHeadlineState,
                description,
                error
            ),
        });
    },

    onTabChanged(tab) {
        // Prevent bug causing Tabs.onChange to be called with bubbling events
        if (isNumber(tab)) {
            this.setState({ selectedTab: tab });
        }
    },

    validateHeadline(value) {
        const validate = this.props.validateHeadline;
        return isFunction(validate) ? validate(value) : '';
    },

    editHeadline(edit = true, disable = null) {
        const { authenticated, updateHeadline } = this.props;
        const state = StateMachine.mapState(authenticated, updateHeadline, edit, disable);
        this.setState({ headlineState: state });
        if (disable === null) {
            this.editDescription(false, edit);
        }
    },

    cancelEditHeadline() {
        this.setState({ headline: this.props.headline, headlineErrorText: '' });
        this.editHeadline(false);
    },

    changeHeadline(event) {
        const value = event.target.value;
        const errorText = this.validateHeadline(value);
        this.setState(StateMachine.changeState('headline', value, errorText));
    },

    saveHeadline(event) {
        event.preventDefault();
        const { headline, headlineState } = this.state;
        if (headlineState !== State.validationFailed) {
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
        const state = StateMachine.mapState(authenticated, updateDescription, edit, disable);
        this.setState({ descriptionState: state });
        if (disable === null) {
            this.editHeadline(false, edit);
        }
    },

    cancelEditDescription() {
        this.setState({ description: this.props.description, descriptionErrorText: '' });
        this.editDescription(false);
    },

    changeDescription(event) {
        const value = event.target.value;
        const errorText = this.validateDescription(value);
        this.setState(StateMachine.changeState('description', value, errorText));
    },

    saveDescription(event) {
        event.preventDefault();
        const { description, descriptionState } = this.state;
        if (descriptionState !== State.validationFailed) {
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
