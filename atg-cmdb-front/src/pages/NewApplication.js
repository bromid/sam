import React from 'react';
import { connect } from 'react-redux';
import NewApplication from '../components/NewApplication';
import * as applicationActions from '../actions/applicationActions';
import { fromGroup } from '../reducers';

const NewApplicationContainer = ({ groupIds = [], handleCreate }) => (
    <div>
        <h2>New application</h2>
        <NewApplication
            groupIds={groupIds}
            onCreate={handleCreate}
        />
    </div>
);

const mapStateToProps = (state) => ({
    groupIds: fromGroup.getIds(state),
});

const Actions = {
    handleCreate: applicationActions.createApplication,
};
export default connect(mapStateToProps, Actions)(NewApplicationContainer);
