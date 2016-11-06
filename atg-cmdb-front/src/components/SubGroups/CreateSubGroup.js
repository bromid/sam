import React from 'react';
import NewGroup from '../NewGroup';

const CreateSubGroup = ({ onCreate, onCancel }) => (
    <div>
        <h3>Create new group</h3>
        <NewGroup onCreate={onCreate} onCancel={onCancel} />
    </div>
);

export default CreateSubGroup;
