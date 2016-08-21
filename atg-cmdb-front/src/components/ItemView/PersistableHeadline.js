import React, { PropTypes } from 'react';
import PersistableField from '../PersistableField';
import EditIconButton from './EditIconButton';

const Headline = ({ value, state, edit }) => (
    <h2 className="editIconWrapper" style={{ position: 'relative', minHeight: 25 }}>
        {value}
        <EditIconButton edit={edit} state={state} style={{ position: 'absolute', top: -7 }} />
    </h2>
);

const PersistableHeadline = React.createClass({
    propTypes: {
        value: PropTypes.string.isRequired,
        state: PropTypes.oneOf(['readonly', 'editable', 'editing', 'saving', 'failed']).isRequired,
        errorText: PropTypes.string,
        edit: PropTypes.func,
        cancel: PropTypes.func,
        save: PropTypes.func,
        change: PropTypes.func,
    },

    onSave(event) {
        const { errorText = '', save } = this.props;
        if (errorText.length > 1) {
            this.fieldRef.focus();
        }
        save(event);
    },

    render() {
        const { value, state, errorText, edit, cancel, change } = this.props;
        const editActive = (state === 'editing');
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
                <Headline value={value} state={state} edit={edit} />
        );
    },
});
export default PersistableHeadline;
