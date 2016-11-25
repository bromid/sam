import React from 'react';
import { connect } from 'react-redux';
import isEmpty from 'lodash/isEmpty';
import { collectionSize } from '../helpers';
import * as applicationValidators from '../validators/applicationValidators';
import * as applicationActions from '../actions/applicationActions';
import LoadingIndicator from '../components/LoadingIndicator';
import Attributes from '../components/Attributes';
import ItemView from '../components/ItemView';
import ApplicationDeployments from '../components/ApplicationDeployment';
import EditableGroupLink from '../components/EditableGroupLink';
import { State } from '../components/EditState';
import * as StateMachine from '../components/EditStateMachine';
import RefreshButton from '../components/ItemView/RefreshButton';
import DeleteButton from '../components/ItemView/DeleteButton';
import { fromApplication, fromGroup, fromAuth } from '../reducers';

const groupId = (group) => (
    (group) ? group.id : ''
);

const Details = ({ groupLink }) => (
    <div>
        <dl>
            <dt>Group</dt>
            <dd>
                {groupLink}
            </dd>
        </dl>
    </div>
);

const Application = (props) => {
    const {
        application: { name, description = '', attributes, meta },
        deployments, patchIsPending, patchError, groupLink,
        isLoading, isAuthenticated, onUpdateName, onUpdateDescription, onRefresh, onDelete,
    } = props;

    const tabs = [
        {
            name: 'Details',
            node: <Details attributes={attributes} groupLink={groupLink} />,
        },
        {
            name: `Deployments ${collectionSize(deployments, '')}`,
            node: <ApplicationDeployments />,
        },
        {
            name: `Attributes ${collectionSize(attributes)}`,
            node: <Attributes attributes={attributes} />,
        },
    ];

    const buttons = [<RefreshButton key="refresh" onClick={onRefresh} />];
    if (isAuthenticated) {
        buttons.push(<DeleteButton key="delete" onClick={onDelete} />);
    }

    return (
        <ItemView
            tabs={tabs}
            headline={name}
            updateHeadline={onUpdateName}
            validateHeadline={applicationValidators.name}
            description={description}
            updateDescription={onUpdateDescription}
            validateDescription={applicationValidators.description}
            meta={meta}
            isLoading={isLoading}
            patchIsPending={patchIsPending}
            patchError={patchError}
            buttons={buttons}
        />
    );
};

const ApplicationContainer = React.createClass({

    getInitialState() {
        const { isAuthenticated, application: { group } = { } } = this.props;
        return {
            group: groupId(group),
            groupErrorText: '',
            groupState: StateMachine.mapState(isAuthenticated, this.updateGroup),
        };
    },

    componentWillReceiveProps(newProps) {
        const {
            isAuthenticated, patchError, patchIsPending,
            application: { group } = { },
        } = newProps;
        const { groupState } = this.state;

        const error = StateMachine.parseError(patchError);

        const newGroupState = StateMachine.mapStateFromCurrent(
            groupState,
            isAuthenticated,
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

    updateName(name) {
        const { patchApplication, application: { id, meta } } = this.props;
        patchApplication(id, { name }, { hash: meta.hash });
    },

    updateDescription(description) {
        const { patchApplication, application: { id, meta } } = this.props;
        patchApplication(id, { description }, { hash: meta.hash });
    },

    editGroup(edit = true) {
        const { isAuthenticated } = this.props;
        const groupState = StateMachine.mapState(isAuthenticated, this.updateGroup, edit);
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
            application, groupIds, deployments, patchIsPending, patchError,
            isLoading, isAuthenticated, fetchApplication, deleteApplication,
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
                deployments={deployments}
                isLoading={isLoading}
                isAuthenticated={isAuthenticated}
                patchIsPending={patchIsPending}
                patchError={patchError}
                groupLink={groupLink}
                onUpdateDescription={this.updateDescription}
                onUpdateName={this.updateName}
                onRefresh={() => fetchApplication(application.id)}
                onDelete={() => deleteApplication(application.id)}
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
    deployments: fromApplication.getDeployments(state),
    isLoading: fromApplication.getCurrentIsPending(state),
    isAuthenticated: fromAuth.getIsAuthenticated(state),
});

const Actions = {
    patchApplication: applicationActions.patchApplication,
    fetchApplication: applicationActions.fetchApplication,
    deleteApplication: applicationActions.deleteApplication,
};
export default connect(mapStateToProps, Actions)(ApplicationContainer);
