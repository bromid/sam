import React from 'react';
import PersistableField from '../PersistableField';
import EditIconButton from './EditIconButton';

const Headline = ({ headline, edit }) => (
    <h2 className="editIconWrapper" style={{ position: 'relative', minHeight: 25 }}>
        {headline}
        <EditIconButton edit={edit} style={{ position: 'absolute', top: -7 }} />
    </h2>
);

const PersistableHeadline = ({ headline, editActive, edit, cancel, save, change }) => (
    (editActive) ?
        <PersistableField
            id="headlineInput"
            change={change}
            save={save}
            cancel={cancel}
            value={headline}
        /> :
        <Headline headline={headline} edit={edit} />
);
export default PersistableHeadline;
