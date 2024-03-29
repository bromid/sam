import React from 'react';
import { connect } from 'react-redux';
import isEmpty from 'lodash/isEmpty';
import { collectionSize } from '../helpers';
import * as assetValidators from '../validators/assetValidators';
import * as assetActions from '../actions/assetActions';
import LoadingIndicator from '../components/LoadingIndicator';
import Attributes from '../components/Attributes';
import ItemView from '../components/ItemView';
import EditableGroupLink from '../components/EditableGroupLink';
import { State } from '../components/EditState';
import * as StateMachine from '../components/EditStateMachine';
import RefreshButton from '../components/ItemView/RefreshButton';
import DeleteButton from '../components/ItemView/DeleteButton';
import { fromAsset, fromGroup, fromAuth } from '../reducers';

const groupId = (group) => (
    (group) ? group.id : ''
);

const Details = ({ groupLink }) => (
    <div>
        <dl>
            <dt>Group</dt>
            {groupLink}
        </dl>
    </div>
);

const Asset = (props) => {
    const {
        asset: { name, description = '', attributes, meta },
        isLoading, isAuthenticated, patchIsPending, patchError, groupLink,
        onUpdateName, onUpdateDescription, onRefresh, onDelete,
    } = props;

    const tabs = [
        {
            name: 'Details',
            node: (
                <Details groupLink={groupLink} attributes={attributes} />
            ),
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
            validateHedline={assetValidators.name}
            description={description}
            updateDescription={onUpdateDescription}
            validateDescription={assetValidators.description}
            meta={meta}
            isLoading={isLoading}
            patchIsPending={patchIsPending}
            patchError={patchError}
            buttons={buttons}
        />
    );
};

const AssetContainer = React.createClass({

    getInitialState() {
        const { isAuthenticated, asset: { group } = { } } = this.props;
        return {
            group: groupId(group),
            groupErrorText: '',
            groupState: StateMachine.mapState(isAuthenticated, this.updateGroup),
        };
    },

    componentWillReceiveProps(newProps) {
        const {
            isAuthenticated, patchError, patchIsPending,
            asset: { group } = { },
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
        const { patchAsset, asset: { id, meta } } = this.props;
        patchAsset(id, { name }, { hash: meta.hash });
    },

    updateDescription(description) {
        const { patchAsset, asset: { id, meta } } = this.props;
        patchAsset(id, { description }, { hash: meta.hash });
    },

    editGroup(edit = true) {
        const { isAuthenticated } = this.props;
        const groupState = StateMachine.mapState(isAuthenticated, this.updateGroup, edit);
        this.setState({ groupState });
    },

    cancelEditGroup() {
        const { asset: { group } = {} } = this.props;
        this.setState({ group: groupId(group), groupErrorText: '' });
        this.editGroup(false);
    },

    changeGroup(group) {
        const value = group.trim().toLocaleLowerCase();
        const errorText = assetValidators.group(value);
        this.setState(StateMachine.changeState('group', value, errorText));
    },

    updateGroup(event) {
        event.preventDefault();
        const { patchAsset, asset: { id, meta } } = this.props;
        const { group, groupState } = this.state;
        if (groupState !== State.validationFailed) {
            this.editGroup(false);
            patchAsset(id, { group }, { hash: meta.hash });
        }
    },

    render() {
        const {
            asset, groupIds, patchIsPending, patchError,
            isLoading, isAuthenticated, fetchAsset, deleteAsset,
        } = this.props;

        if (isLoading && isEmpty(asset)) return <LoadingIndicator />;

        const { group, groupErrorText, groupState } = this.state;
        const groupLink = (
            <EditableGroupLink
                group={asset.group}
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
            <Asset
                asset={asset}
                isLoading={isLoading}
                isAuthenticated={isAuthenticated}
                patchIsPending={patchIsPending}
                patchError={patchError}
                groupLink={groupLink}
                onUpdateDescription={this.updateDescription}
                onUpdateName={this.updateName}
                onRefresh={() => fetchAsset(asset.id)}
                onDelete={() => deleteAsset(asset.id)}
            />
        );
    },
});

const mapStateToProps = (state) => ({
    asset: fromAsset.getCurrent(state),
    fetchError: fromAsset.getCurrentError(state),
    patchResult: fromAsset.getPatchResult(state),
    patchError: fromAsset.getPatchResultError(state),
    patchIsPending: fromAsset.getPatchResultIsPending(state),
    groupIds: fromGroup.getIds(state),
    isLoading: fromAsset.getCurrentIsPending(state),
    isAuthenticated: fromAuth.getIsAuthenticated(state),
});

const Actions = {
    patchAsset: assetActions.patchAsset,
    fetchAsset: assetActions.fetchAsset,
    deleteAsset: assetActions.deleteAsset,
};
export default connect(mapStateToProps, Actions)(AssetContainer);
