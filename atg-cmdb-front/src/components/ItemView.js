import React, { PropTypes } from 'react';
import ReactMarkdown from 'react-markdown';
import isFunction from 'lodash/isFunction';
import { Tabs, Tab } from 'material-ui/Tabs';
import TextField from 'material-ui/TextField';
import IconButton from 'material-ui/IconButton';
import FlatButton from 'material-ui/FlatButton';
import RaisedButton from 'material-ui/RaisedButton';
import EditIcon from 'material-ui/svg-icons/image/edit';
import { blue800 } from 'material-ui/styles/colors';
import { borderStyle, containerStyle, flexWrapperStyle } from '../style';
import Meta from './Meta';
import { Tags } from './Tag';

function EditIconButton({ style, edit }) {
    if (!isFunction(edit)) return null;
    return (
        <IconButton
            className="editIcon"
            tooltip="Edit"
            onTouchTap={edit}
            style={{ ...style, display: 'none', padding: 0 }}
        >
            <EditIcon color={blue800} />
        </IconButton>
    );
}

function TextFieldForm({ id, value, change, save, cancel, multiLine = false }) {
    const formStyleSingleLine = {
        flex: 1,
        display: 'flex',
        flexDirection: 'row',
        alignItems: 'baseline',
    };

    const formStyleMultiLine = {
        flex: 1,
        display: 'flex',
        flexDirection: 'column',
        alignItems: 'stretch',
    };

    return (
        <form style={(multiLine) ? formStyleMultiLine : formStyleSingleLine} onSubmit={save}>
            <TextField
                id={id}
                style={{ flex: 1 }}
                fullWidth={true}
                multiLine={multiLine}
                value={value}
                onChange={change}
            />
            <div style={{ display: 'flex', justifyContent: 'flex-end' }}>
                <FlatButton
                    label="Cancel"
                    secondary={false}
                    onTouchTap={cancel}
                />
                <RaisedButton
                    label="Save"
                    secondary={true}
                    primary={false}
                    onTouchTap={save}
                />
            </div>
        </form>
    );
}

function Description({ description, edit }) {
    if (!description) return <div />;
    return (
        <div style={{ flex: 1 }}>
            <EditIconButton edit={edit} style={{ float: 'right', right: -10, top: -10 }} />
            <ReactMarkdown skipHtml={true} source={description} />
        </div>
    );
}

function Headline({ headline, edit }) {
    return (
        <h2 className="editIconWrapper" style={{ position: 'relative', minHeight: 25 }}>
            {headline}
            <EditIconButton edit={edit} style={{ position: 'absolute', top: -7 }} />
        </h2>
    );
}

function EditableDescription({ description, editActive, edit, cancel, save, change }) {
    const descriptionWrapperStyle = {
        ...borderStyle,
        ...flexWrapperStyle,
        flex: 1,
        padding: 10,
        margin: '15px 15px 15px 0',
    };

    return (
        <div className="editIconWrapper" style={descriptionWrapperStyle}>
            {(editActive) ?
                <TextFieldForm
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

function EditableHeadline({ headline, editActive, edit, cancel, save, change }) {
    return (editActive) ?
        <TextFieldForm
            id="headlineInput"
            change={change}
            save={save}
            cancel={cancel}
            value={headline}
        /> :
        <Headline headline={headline} edit={edit} />;
}

const ItemView = React.createClass({
    propTypes: {
        headline: PropTypes.string.isRequired,
        updateHeadline: PropTypes.func,
        description: PropTypes.string.isRequired,
        updateDescription: PropTypes.func,
        meta: PropTypes.object,
        metaOpen: PropTypes.bool,
        toggleMeta: PropTypes.func,
        tabs: PropTypes.array,
        onTabChanged: PropTypes.func,
        selectedTab: PropTypes.string,
        tags: PropTypes.array,
        onTagDelete: PropTypes.func,
    },

    getInitialState() {
        const { description, updateDescription, headline, updateHeadline } = this.props;
        return {
            headline,
            headlineEditable: isFunction(updateHeadline),
            headlineEditActive: false,
            description,
            descriptionEditable: isFunction(updateDescription),
            descriptionEditActive: false,
        };
    },

    editHeadline(edit = true) {
        this.setState({
            headlineEditActive: edit,
        });
    },

    cancelEditHeadline() {
        this.setState({
            headline: this.props.headline,
        });
        this.editHeadline(false);
    },

    saveHeadline(event) {
        event.preventDefault();
        this.editHeadline(false);
        this.props.updateHeadline(this.state.headline);
    },

    changeHeadline(event) {
        this.setState({
            headline: event.target.value,
        });
    },

    editDescription(edit = true) {
        this.setState({
            descriptionEditActive: edit,
        });
    },

    cancelEditDescription() {
        this.setState({
            description: this.props.description,
        });
        this.editDescription(false);
    },

    saveDescription(event) {
        event.preventDefault();
        this.editDescription(false);
        this.props.updateDescription(this.state.description);
    },

    changeDescription(event) {
        this.setState({
            description: event.target.value,
        });
    },

    render() {
        const {
            tags, onTagDelete,
            meta, metaOpen, toggleMeta,
            tabs, selectedTab, onTabChanged,
        } = this.props;

        const {
            headline, headlineEditable, headlineEditActive,
            description, descriptionEditable, descriptionEditActive,
        } = this.state;

        return (
            <div>
                <EditableHeadline
                    headline={headline}
                    editActive={headlineEditActive}
                    edit={(headlineEditable) ? this.editHeadline : null}
                    cancel={this.cancelEditHeadline}
                    save={this.saveHeadline}
                    change={this.changeHeadline}
                />
                <Tags tags={tags} onDelete={onTagDelete} />
                <div style={flexWrapperStyle}>
                    <EditableDescription
                        description={description}
                        editActive={descriptionEditActive}
                        edit={(descriptionEditable) ? this.editDescription : null}
                        cancel={this.cancelEditDescription}
                        save={this.saveDescription}
                        change={this.changeDescription}
                    />
                    <Meta meta={meta} open={metaOpen} toggle={toggleMeta} />
                </div>
                <Tabs value={selectedTab} onChange={onTabChanged}>
                    {tabs.map((tab, index) => (
                        <Tab key={tab.name} value={index} label={tab.name}>
                            <div style={containerStyle}>
                                {tab.node}
                            </div>
                        </Tab>
                    ))}
                </Tabs>
            </div>
        );
    },
});

export default ItemView;
