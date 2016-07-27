import React, { PropTypes } from 'react';
import isFunction from 'lodash/isFunction';
import { Tabs, Tab } from 'material-ui/Tabs';
import LoadingIndicator from '../LoadingIndicator';
import { containerStyle, flexWrapperStyle } from '../../style';
import Notifier from '../Notifier';
import Meta from '../Meta';
import { Tags } from '../Tag';
import PersistableDescription from './PersistableDescription';
import PersistableHeadline from './PersistableHeadline';

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
        tags: PropTypes.array,
        onTagDelete: PropTypes.func,
        notification: PropTypes.oneOfType([PropTypes.object, PropTypes.func]),
        isLoading: PropTypes.bool,
    },

    getInitialState() {
        const { description, updateDescription, headline, updateHeadline } = this.props;
        return {
            selectedTab: 0,
            headline,
            headlineEditable: isFunction(updateHeadline),
            headlineEditActive: false,
            description,
            descriptionEditable: isFunction(updateDescription),
            descriptionEditActive: false,
        };
    },

    componentWillReceiveProps(newProps) {
        const { description, updateDescription, headline, updateHeadline } = newProps;
        this.setState({
            headline,
            headlineEditable: isFunction(updateHeadline),
            description,
            descriptionEditable: isFunction(updateDescription),
        });
    },

    onTabChanged(tab) {
        this.setState({
            selectedTab: tab,
        });
    },

    editHeadline(edit = true) {
        this.setState({
            headlineEditActive: edit,
        });
    },

    cancelEditHeadline() {
        const { headline } = this.props;
        this.setState({ headline });
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
        const { description } = this.props;
        this.setState({ description });
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
            isLoading, notification, tabs,
            tags, onTagDelete,
            meta, metaOpen, toggleMeta,
        } = this.props;

        const {
            headline, headlineEditable, headlineEditActive,
            description, descriptionEditable, descriptionEditActive,
        } = this.state;

        return (
            <div>
                {isLoading && <LoadingIndicator />}
                <PersistableHeadline
                    headline={headline}
                    editActive={headlineEditActive}
                    edit={(headlineEditable) ? this.editHeadline : null}
                    cancel={this.cancelEditHeadline}
                    save={this.saveHeadline}
                    change={this.changeHeadline}
                />
                <Tags tags={tags} onDelete={onTagDelete} />
                <div style={flexWrapperStyle}>
                    <PersistableDescription
                        description={description}
                        editActive={descriptionEditActive}
                        edit={(descriptionEditable) ? this.editDescription : null}
                        cancel={this.cancelEditDescription}
                        save={this.saveDescription}
                        change={this.changeDescription}
                    />
                    <Meta meta={meta} open={metaOpen} toggle={toggleMeta} />
                </div>
                <Tabs value={this.state.selectedTab} onChange={this.onTabChanged}>
                    {tabs.map((tab, index) => (
                        <Tab key={tab.name} value={index} label={tab.name}>
                            <div style={containerStyle}>
                                {tab.node}
                            </div>
                        </Tab>
                    ))}
                </Tabs>
                <Notifier notification={notification} />
            </div>
        );
    },
});

export default ItemView;
