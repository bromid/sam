import React from 'react';
import { connect } from 'react-redux';
import NewGroup from '../components/NewGroup';
import * as groupActions from '../actions/groupActions';

const NewGroupContainer = ({ handleCreate }) => (
    <div>
        <h2>New group</h2>
        <NewGroup onCreate={handleCreate} />
    </div>
);

const Actions = {
    handleCreate: groupActions.createGroup,
};
export default connect(null, Actions)(NewGroupContainer);
