import React from 'react';
import JSONEditor from 'jsoneditor';
import '../../node_modules/jsoneditor/dist/jsoneditor.css';

const Attributes = React.createClass({

    componentWillReceiveProps(nextProps) {
        const { attributes } = nextProps;
        if (this.editor) {
            this.editor.set(attributes);
        }
    },

    componentWillUnmount() {
        if (this.editor) {
            this.editor.destroy();
        }
    },

    createEditor(container, attributes, search = true) {
        if (container && !this.editor) {
            const options = {
                search,
                mode: 'view',
                name: 'Attributes',
                history: false,
            };
            this.editor = new JSONEditor(container, options, attributes);
        }
    },

    render() {
        const { attributes, search } = this.props;
        if (!attributes) return <p>No attributes</p>;
        return <div ref={(ref) => this.createEditor(ref, attributes, search)} />;
    },
});

const AttributesContainer = ({ attributes, search }) => (
    <dl>
        <dt>Attributes</dt>
        <dd>
            <Attributes attributes={attributes} search={search} />
        </dd>
    </dl>
);
export default AttributesContainer;
