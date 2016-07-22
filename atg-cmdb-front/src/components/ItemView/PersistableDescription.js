import React from 'react';
import ReactMarkdown from 'react-markdown';
import { borderStyle, flexWrapperStyle } from '../../style';
import PersistableField from '../PersistableField';
import EditIconButton from './EditIconButton';

function Description({ description, edit }) {
    return (
        <div style={{ flex: 1 }}>
            <EditIconButton edit={edit} style={{ float: 'right', right: -10, top: -10 }} />
            <ReactMarkdown skipHtml={true} source={description} />
        </div>
    );
}

export default function PersistableDescription(props) {
    const { description, editActive, edit, cancel, save, change } = props;
    const descriptionWrapperStyle = {
        ...borderStyle,
        ...flexWrapperStyle,
        flex: 1,
        minHeight: 75,
        padding: 10,
        margin: '15px 15px 15px 0',
    };

    return (
        <div className="editIconWrapper" style={descriptionWrapperStyle}>
            {(editActive) ?
                <PersistableField
                    id="descriptionInput"
                    change={change}
                    save={save}
                    cancel={cancel}
                    value={description}
                    multiLine={true}
                /> :
                <Description description={description} edit={edit} />
            }
        </div>
    );
}
