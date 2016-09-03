import React from 'react';
import { connect } from 'react-redux';
import isEmpty from 'lodash/isEmpty';
import * as applicationValidators from '../validators/applicationValidators';
import * as applicationActions from '../actions/applicationActions';
import LoadingIndicator from '../components/LoadingIndicator';
import Attributes from '../components/Attributes';
import ItemView from '../components/ItemView';
import ApplicationDeployments from '../components/ApplicationDeployment';
import EditableGroupLink from '../components/EditableGroupLink';
import { State } from '../components/EditState';
import * as StateMachine from '../components/EditStateMachine';
import { fromApplication, fromGroup, getAuthenticated } from '../reducers';

const groupId = (group) => (
    (group) ? group.id : ''
);

const Details = ({ attributes, groupLink }) => (
    <div>
        <dl>
            <dt>Group</dt>
            <dd>
                {groupLink}
            </dd>
        </dl>
        <Attributes attributes={attributes} />
    </div>
);

const Application = (props) => {
    const {
        application: { id, name, description = '', attributes, meta },
        isLoading, patchIsPending, patchError, updateName, updateDescription, groupLink,
    } = props;

    const tabs = [
        {
            name: 'Details',
            node: <Details attributes={attributes} groupLink={groupLink} />,
        },
        {
            name: 'Deployments',
            node: <ApplicationDeployments id={id} />,
        },
    ];

    return (
        <ItemView
            tabs={tabs}
            headline={name}
            updateHeadline={updateName}
            validateHeadline={applicationValidators.name}
            description={description}
            updateDescription={updateDescription}
            validateDescription={applicationValidators.description}
            meta={meta}
            isLoading={isLoading}
            patchIsPending={patchIsPending}
            patchError={patchError}
        />
    );
};

const ApplicationContainer = React.createClass({

    getInitialState() {
        const { authenticated, application: { group } = { } } = this.props;
        return {
            group: groupId(group),
            groupErrorText: '',
            groupState: StateMachine.mapState(authenticated, this.updateGroup),
        };
    },

    componentWillReceiveProps(newProps) {
        const {
            authenticated, patchError, patchIsPending,
            application: { group } = { },
        } = newProps;
        const { groupState } = this.state;

        const error = StateMachine.parseError(patchError);

        const newGroupState = StateMachine.mapStateFromCurrent(
            groupState,
            authenticated,
            this.updateGroup,
            patchIsPending,
            error.isError
        );

        this.setState({
            ...StateMachine.newPropsState(
                'group',
                newGroupState,
                State.readonly,
                groupId(group),
                error
            ),
        });
    },

    updateDescription(description) {
        const { patchApplication, application: { id, meta } } = this.props;
        patchApplication(id, { description }, { hash: meta.hash });
    },

    updateName(name) {
        const { patchApplication, application: { id, meta } } = this.props;
        patchApplication(id, { name }, { hash: meta.hash });
    },

    editGroup(edit = true) {
        const { authenticated } = this.props;
        const groupState = StateMachine.mapState(authenticated, this.updateGroup, edit);
        this.setState({ groupState });
    },

    cancelEditGroup() {
        const { application: { group } = {} } = this.props;
        this.setState({ group: groupId(group), groupErrorText: '' });
        this.editGroup(false);
    },

    changeGroup(group) {
        const value = group.trim().toLocaleLowerCase();
        const errorText = applicationValidators.group(value);
        this.setState(StateMachine.changeState('group', value, errorText));
    },

    updateGroup(event) {
        event.preventDefault();
        const { patchApplication, application: { id, meta } } = this.props;
        const { group, groupState } = this.state;
        if (groupState !== State.validationFailed) {
            this.editGroup(false);
            patchApplication(id, { group }, { hash: meta.hash });
        }
    },

    render() {
        const {
            application, groupIds, isLoading, patchIsPending, patchError,
        } = this.props;

        if (isLoading && isEmpty(application)) return <LoadingIndicator />;

        const { group, groupErrorText, groupState } = this.state;
        const groupLink = (
            <EditableGroupLink
                group={application.group}
                groupIds={groupIds}
                value={group}
                errorText={groupErrorText}
                state={groupState}
                edit={this.editGroup}
                change={this.changeGroup}
                cancel={this.cancelEditGroup}
                save={this.updateGroup}
            />);
        return (
            <Application
                application={application}
                isLoading={isLoading}
                patchIsPending={patchIsPending}
                patchError={patchError}
                groupLink={groupLink}
                updateDescription={this.updateDescription}
                updateName={this.updateName}
            />
        );
    },
});

const mapStateToProps = (state) => ({
    application: fromApplication.getCurrent(state),
    fetchError: fromApplication.getCurrentError(state),
    patchResult: fromApplication.getPatchResult(state),
    patchError: fromApplication.getPatchResultError(state),
    patchIsPending: fromApplication.getPatchResultIsPending(state),
    groupIds: fromGroup.getIds(state),
    isLoading: fromApplication.getCurrentIsPending(state),
    authenticated: getAuthenticated(state),
});

const Actions = {
    patchApplication: applicationActions.patchApplication,
};
export default connect(mapStateToProps, Actions)(ApplicationContainer);
