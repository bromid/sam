import React from 'react';
import NewGroup from '../NewGroup';

const CreateSubGroup = ({ onCreate, onCancel, createIsPending, createError }) => (
    <div>
        <h3>Create new group</h3>
        <NewGroup
            onCreate={onCreate}
            onCancel={onCancel}
            createIsPending={createIsPending}
            createError={createError}
        />
    </div>
);

export default CreateSubGroup;
