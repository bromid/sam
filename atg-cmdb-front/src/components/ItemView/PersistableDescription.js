import React from 'react';
import { withRouter } from 'react-router';
import ReactMarkdown from 'react-markdown';
import { borderStyle, flexWrapperStyle } from '../../style';
import PersistableField from '../PersistableField';
import EditIconButton from './EditIconButton';

const Description = ({ value, edit, addHandler }) => (
    <div style={{ flex: 1 }} ref={addHandler}>
        <EditIconButton edit={edit} style={{ float: 'right', right: -10, top: -10 }} />
        <ReactMarkdown skipHtml={true} source={value} />
    </div>
);

const PersistableDescription = React.createClass({

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
                    <Description
                        value={value}
                        edit={edit}
                        addHandler={this.addLinkWrapperHandler}
                    />
                }
            </div>
        );
    },
});
export default withRouter(PersistableDescription);
