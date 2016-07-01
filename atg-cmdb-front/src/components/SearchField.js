import React, {PropTypes} from 'react';
import SearchIcon from 'material-ui/svg-icons/action/search';
import { blue800 } from 'material-ui/styles/colors';
import SearchResultDialog from './SearchResultDialog';

const searchStyles = {
    wrapper: {
        position: "relative",
        marginRight: 10,
        marginTop: 5
    },
    icon: {
        position: "absolute",
        right: 5,
        top: 8
    },
    input: {
        fontSize: 14,
        padding: 10,
        boxShadow: "0 2px 10px rgba(1, 1, 1, 0.4)",
        borderRadius: "4px",
        minWidth: 200,
        fontFamily: "Roboto, sans-serif",
        border: "1px solid transparent"
    }
};

const SearchField = React.createClass({
    propTypes: {
        fetchSearch: PropTypes.func,
        searchResults: PropTypes.object,
        searchIsLoading: PropTypes.bool
    },

    getInitialState() {
        return {
            showIcon: true,
            modalOpen: false
        };
    },

    handleFocus() {
        this.setState({showIcon: false});
    },

    handleBlur() {
        this.setState({showIcon: true});
    },

    handleCloseModal() {
        this.setState({modalOpen: false});
    },

    handleSubmit(event) {
        event.preventDefault();
        const {fetchSearch} = this.props;
        const searchString = this.refs.input.value;
        fetchSearch(searchString);
        this.setState({modalOpen: true});
    },

    render() {
        const {showIcon, modalOpen} = this.state;
        const {searchResults, searchIsLoading} = this.props;
        return (
            <div style={searchStyles.wrapper}>
                <form onSubmit={this.handleSubmit}>
                    <input ref="input" onFocus={this.handleFocus} onBlur={this.handleBlur} style={searchStyles.input} type="text" placeholder="Search..." />
                    {showIcon && <SearchIcon style={searchStyles.icon} color={blue800} />}
                </form>
                <SearchResultDialog searchResults={searchResults}
                                    searchIsLoading={searchIsLoading}
                                    modalOpen={modalOpen}
                                    handleCloseModal={this.handleCloseModal} />
            </div>
        );
    }
});

export default SearchField;