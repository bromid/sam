import * as React from 'react';

const Queries = {
    xsPlus: window.matchMedia('(min-width: 480px)'),
    smPlus: window.matchMedia('(min-width: 768px)'),
    mdPlus: window.matchMedia('(min-width: 992px)'),
    lgPlus: window.matchMedia('(min-width: 1200)'),
};

function getMatches(queries) {
    return Object.keys(queries)
        .reduce((acc, key) => ({ ...acc, [key]: queries[key].matches }), {});
}

export default (Component) => (
    React.createClass({
        getInitialState() {
            return getMatches(Queries);
        },

        componentWillMount() {
            Object.keys(Queries).forEach((key) => Queries[key].addListener(this.handler));
        },

        componentWillUnmount() {
            Object.keys(Queries).forEach((key) => Queries[key].removeListener(this.handler));
        },

        handler() {
            this.setState(getMatches(Queries));
        },

        render() {
            return <Component {...this.props} {...this.state} />;
        },
    })
);
