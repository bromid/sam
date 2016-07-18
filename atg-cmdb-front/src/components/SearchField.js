import React, { PropTypes } from 'react';
import IconButton from 'material-ui/IconButton';
import SearchIcon from 'material-ui/svg-icons/action/search';
import ClearIcon from 'material-ui/svg-icons/content/clear';
import { blue800 } from 'material-ui/styles/colors';
import isEmpty from 'lodash/isEmpty';
import SearchResultDialog from './SearchResultDialog';

const searchStyles = {
    wrapper: {
        position: 'relative',
        marginRight: 16,
    },
    icon: {
        position: 'absolute',
        padding: 0,
        top: 0,
        right: 0,
    },
    input: {
        fontSize: 14,
        padding: 10,
        boxShadow: '0 2px 10px rgba(1, 1, 1, 0.4)',
        borderRadius: '4px',
        minWidth: 200,
        fontFamily: 'Roboto, sans-serif',
        border: '1px solid transparent',
    },
};

const iconSearchButtonId = 'search-button';
const iconClearButtonId = 'search-clear-button';

const SearchField = React.createClass({
    propTypes: {
        fetchSearch: PropTypes.func,
        searchResults: PropTypes.object,
        searchResultsIsLoading: PropTypes.bool,
    },

    getInitialState() {
        return {
            showSearchIcon: true,
            showClearIcon: false,
            modalOpen: false,
        };
    },

    handleFocus() {
        this.setState({ showSearchIcon: false });
        this.handleChange();
    },

    handleBlur(event) {
        // Don't hide the clear button if it's the target when the input loses focus
        if (!event.relatedTarget || event.relatedTarget.id !== iconClearButtonId) {
            this.setState({
                showSearchIcon: true,
                showClearIcon: false,
            });
        }
    },

    handleChange() {
        if (!isEmpty(this.refs.input.value)) {
            this.setState({
                showClearIcon: true,
            });
        } else if (this.state.showClearIcon) {
            this.setState({
                showClearIcon: false,
            });
        }
    },

    handleCloseModal() {
        this.setState({ modalOpen: false });
    },

    handleSubmit(event) {
        event.preventDefault();
        const searchString = this.refs.input.value;
        if (!isEmpty(searchString)) {
            this.props.fetchSearch(searchString);
            this.setState({ modalOpen: true });
        }
    },

    clear() {
        this.refs.input.value = null;
        this.refs.input.focus();
    },

    render() {
        const { showSearchIcon, showClearIcon, modalOpen } = this.state;
        const { searchResults, searchResultsIsLoading } = this.props;
        return (
            <div style={searchStyles.wrapper}>
                <form onSubmit={this.handleSubmit}>
                    <input
                        ref="input"
                        onFocus={this.handleFocus}
                        onBlur={this.handleBlur}
                        onChange={this.handleChange}
                        style={searchStyles.input}
                        type="text"
                        placeholder="Search..."
                    />
                    {showSearchIcon && <IconButton
                        id={iconSearchButtonId}
                        tooltip="Search"
                        style={searchStyles.icon}
                        onTouchTap={this.handleSubmit}
                        children={<SearchIcon color={blue800} />}
                    />}
                    {showClearIcon && <IconButton
                        id={iconClearButtonId}
                        tooltip="Clear"
                        style={searchStyles.icon}
                        onTouchTap={this.clear}
                        children={<ClearIcon color={blue800} />}
                    />}
                </form>
                <SearchResultDialog
                    searchResults={searchResults}
                    searchResultsIsLoading={searchResultsIsLoading}
                    modalOpen={modalOpen}
                    handleCloseModal={this.handleCloseModal}
                />
            </div>
        );
    },
});

export default SearchField;
