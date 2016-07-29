import React from 'react';
import ReactMarkdown from 'react-markdown';
import { borderStyle, flexWrapperStyle } from '../../style';
import PersistableField from '../PersistableField';
import EditIconButton from './EditIconButton';

const Description = ({ value, edit }) => (
    <div style={{ flex: 1 }}>
        <EditIconButton edit={edit} style={{ float: 'right', right: -10, top: -10 }} />
        <ReactMarkdown skipHtml={true} source={value} />
    </div>
);

const PersistableDescription = React.createClass({

    onSave(event) {
        const { errorText = '', save } = this.props;
        if (errorText.length > 1) {
            this.fieldRef.focus();
        }
        save(event);
    },

    render() {
        const { value, errorText, editActive, edit, cancel, change } = this.props;
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
                        value={value}
                        errorText={errorText}
                        change={change}
                        save={this.onSave}
                        cancel={cancel}
                        fieldRef={(ref) => (this.fieldRef = ref)}
                        multiLine={true}
                    /> :
                    <Description value={value} edit={edit} />
                }
            </div>
        );
    },
});
export default PersistableDescription;
