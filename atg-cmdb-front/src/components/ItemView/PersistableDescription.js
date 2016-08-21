import React, { PropTypes } from 'react';
import { withRouter } from 'react-router';
import ReactMarkdown from 'react-markdown';
import { borderStyle, flexWrapperStyle } from '../../style';
import PersistableField from '../PersistableField';
import EditIconButton from './EditIconButton';

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
        state: PropTypes.oneOf(['readonly', 'editable', 'editing', 'saving', 'failed']).isRequired,
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

        const editActive = (state === 'editing');
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
