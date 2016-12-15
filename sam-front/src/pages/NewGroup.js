import React from 'react';
import { connect } from 'react-redux';
import NewGroup from '../components/NewGroup';
import * as groupActions from '../actions/groupActions';
import { fromGroup } from '../reducers';

const NewGroupContainer = ({ handleCreate, createIsPending }) => (
    <div>
        <h2>New group</h2>
        <NewGroup onCreate={handleCreate} createIsPending={createIsPending} />
    </div>
);

const mapStateToProps = (state) => ({
    createIsPending: fromGroup.getCreateResultIsPending(state),
});

const Actions = {
    handleCreate: groupActions.createGroup,
};
export default connect(mapStateToProps, Actions)(NewGroupContainer);
