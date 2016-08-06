import React from 'react';
import PersistableField from '../PersistableField';
import EditIconButton from './EditIconButton';

const Headline = ({ value, edit }) => (
    <h2 className="editIconWrapper" style={{ position: 'relative', minHeight: 25 }}>
        {value}
        <EditIconButton edit={edit} style={{ position: 'absolute', top: -7 }} />
    </h2>
);

const PersistableHeadline = React.createClass({

    onSave(event) {
        const { errorText = '', save } = this.props;
        if (errorText.length > 1) {
            this.fieldRef.focus();
        }
        save(event);
    },

    render() {
        const { value, errorText, editActive, edit, cancel, change } = this.props;
        return (
            (editActive) ?
                <PersistableField
                    id="headlineInput"
                    value={value}
                    errorText={errorText}
                    change={change}
                    save={this.onSave}
                    cancel={cancel}
                    fieldRef={(ref) => (this.fieldRef = ref)}
                /> :
                <Headline value={value} edit={edit} />
        );
    },
});
export default PersistableHeadline;
