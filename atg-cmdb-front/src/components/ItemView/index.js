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
import DeleteButton from './DeleteButton';
import RefreshButton from './RefreshButton';
import { State, isShowEditForm } from '../EditState';
import * as StateMachine from '../EditStateMachine';
import { fromAuth, getIsMetaOpen } from '../../reducers';

const Buttons = ({ isAuthenticated, headlineState, onRefresh, onDelete }) => {
    if (isShowEditForm(headlineState)) {
        return null;
    }
    return (
        <div style={{ ...flexWrapperStyle, position: 'absolute', top: -10, right: 0 }}>
            <RefreshButton onRefresh={onRefresh} />
            {isAuthenticated && <DeleteButton onDelete={onDelete} />}
        </div>
    );
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
        onDelete: PropTypes.func,
        onRefresh: PropTypes.func,
    },

    getInitialState() {
        const {
            isAuthenticated,
            description, updateDescription,
            headline, updateHeadline,
        } = this.props;

        return {
            selectedTab: 0,
            headline,
            headlineErrorText: '',
            headlineState: StateMachine.mapState(isAuthenticated, updateHeadline),
            description,
            descriptionErrorText: '',
            descriptionState: StateMachine.mapState(isAuthenticated, updateDescription),
        };
    },

    componentWillReceiveProps(newProps) {
        const {
            isAuthenticated, patchIsPending, patchError,
            description, updateDescription,
            headline, updateHeadline,
        } = newProps;

        const {
            headlineState, descriptionState,
        } = this.state;

        const error = StateMachine.parseError(patchError);

        const newHeadlineState = StateMachine.mapStateFromCurrent(
            headlineState,
            isAuthenticated,
            updateHeadline,
            patchIsPending,
            error.isError
        );

        const newDescState = StateMachine.mapStateFromCurrent(
            descriptionState,
            isAuthenticated,
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
        const { isAuthenticated, updateHeadline } = this.props;
        const state = StateMachine.mapState(isAuthenticated, updateHeadline, edit, disable);
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
        const { isAuthenticated, updateDescription } = this.props;
        const state = StateMachine.mapState(isAuthenticated, updateDescription, edit, disable);
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
            isAuthenticated, isLoading, tabs,
            tags, onTagDelete,
            meta, metaOpen, toggleMeta,
            onRefresh, onDelete,
        } = this.props;

        const {
            headline, headlineErrorText, headlineState,
            description, descriptionErrorText, descriptionState,
        } = this.state;

        return (
            <div style={{ position: 'relative' }}>
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
                <Buttons
                    isAuthenticated={isAuthenticated}
                    headlineState={headlineState}
                    onRefresh={onRefresh}
                    onDelete={onDelete}
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

const mapStateToProps = (state) => ({
    metaOpen: getIsMetaOpen(state),
    isAuthenticated: fromAuth.getIsAuthenticated(state),
});

const Actions = {
    toggleMeta: metaActions.toggleMeta,
};
export default connect(mapStateToProps, Actions)(ItemViewContainer);
