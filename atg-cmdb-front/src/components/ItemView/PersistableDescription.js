import React, { PropTypes } from 'react';
import { withRouter } from 'react-router';
import ReactMarkdown from 'react-markdown';
import TextField from 'material-ui/TextField';
import { borderStyle, flexWrapperStyle } from '../../style';
import SaveCancelForm from '../SaveCancelForm';
import EditIconButton from '../EditIconButton';
import { isShowEditForm, AllStates } from '../EditState';

const editIconStyle = { float: 'right', position: 'relative', right: -10, top: -10 };

const Description = ({ value, state, edit, addHandler }) => (
    <div style={{ flex: 1 }} ref={addHandler}>
        <EditIconButton edit={edit} state={state} style={editIconStyle} />
        <ReactMarkdown skipHtml={true} source={value} />
    </div>
);

const PersistableDescription = React.createClass({
    propTypes: {
        value: PropTypes.string.isRequired,
        state: PropTypes.oneOf(AllStates).isRequired,
        errorText: PropTypes.string,
        edit: PropTypes.func,
        cancel: PropTypes.func,
        save: PropTypes.func,
        change: PropTypes.func,
    },

    componentWillUnmount() {
        if (this.linkWrapper) {
            this.linkWrapper.removeEventListener('click', this.handleClick);
        }
    },

    onSave(event) {
        const { errorText = '', save } = this.props;
        if (errorText.length > 1) {
            this.fieldRef.focus();
        }
        save(event);
    },

    addLinkWrapperHandler(ref) {
        if (ref) {
            ref.addEventListener('click', this.handleClick, false);
        }
        this.linkWrapper = ref;
    },

    handleClick(event) {
        const target = event.target;
        const linkClick = target.tagName === 'A';
        const clickModifier = event.ctrlKey || event.altKey || event.shiftKey;
        if (!clickModifier && linkClick && target.origin === window.location.origin) {
            event.preventDefault();
            this.props.router.push(target.pathname + target.search + target.hash);
        }
    },

    render() {
        const { value, state, errorText, edit, cancel, change } = this.props;
        const descriptionWrapperStyle = {
            ...borderStyle,
            ...flexWrapperStyle,
            flex: 1,
            minHeight: 75,
            padding: 10,
            margin: '15px 15px 15px 0',
            position: 'relative',
        };

        return (
            <div className="editIconWrapper" style={descriptionWrapperStyle}>
                {isShowEditForm(state) ?
                    <SaveCancelForm columnStyle={true} cancel={cancel} save={this.onSave}>
                        <TextField
                            value={value}
                            errorText={errorText}
                            onChange={change}
                            id="descriptionInput"
                            hintText="Description"
                            hintStyle={{ top: 12, bottom: 'inherit' }}
                            ref={(ref) => (this.fieldRef = ref)}
                            textareaStyle={{ minHeight: 150 }}
                            multiLine={true}
                            fullWidth={true}
                        />
                    </SaveCancelForm> :
                    <Description
                        value={value}
                        state={state}
                        edit={edit}
                        addHandler={this.addLinkWrapperHandler}
                    />
                }
            </div>
        );
    },
});
export default withRouter(PersistableDescription);
