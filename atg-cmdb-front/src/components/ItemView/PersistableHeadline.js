import React, { PropTypes } from 'react';
import TextField from 'material-ui/TextField';
import SaveCancelForm from '../SaveCancelForm';
import EditIconButton from '../EditIconButton';
import { isShowEditForm, AllStates } from '../EditState';

const Headline = ({ value, state, edit }) => (
    <h2 className="editIconWrapper" style={{ position: 'relative', minHeight: 25 }}>
        {value}
        <EditIconButton edit={edit} state={state} style={{ position: 'absolute', top: -7 }} />
    </h2>
);

const PersistableHeadline = React.createClass({
    propTypes: {
        value: PropTypes.string.isRequired,
        state: PropTypes.oneOf(AllStates).isRequired,
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
        return (isShowEditForm(state) ?
            <SaveCancelForm cancel={cancel} save={this.onSave}>
                <TextField
                    value={value}
                    errorText={errorText}
                    onChange={change}
                    id="headlineInput"
                    hintText="Name"
                    ref={(ref) => (this.fieldRef = ref)}
                    fullWidth={true}
                />
            </SaveCancelForm> :
            <Headline value={value} state={state} edit={edit} />
        );
    },
});
export default PersistableHeadline;
