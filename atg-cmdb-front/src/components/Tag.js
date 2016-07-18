import React from 'react';
import Chip from 'material-ui/Chip';
import AutoComplete from 'material-ui/AutoComplete';
import isObject from 'lodash/isObject';
import isFunction from 'lodash/isFunction';
import { flexWrapperStyle } from '../style';

const tagStyle = {
    margin: '0.5em 0.5em 0 0',
    height: 32,
};

function getTagName(tag) {
    return isObject(tag) ? tag.name : tag;
}

export function Tags({ tags, onDelete, getName = getTagName }) {
    if (!tags) return null;

    tags = [].concat(tags); // eslint-disable-line no-param-reassign

    return (
        <div style={{ ...flexWrapperStyle, alignItems: 'baseline' }}>
            {tags.map(tag => (
                <Tag
                    key={getName(tag)}
                    name={getName(tag)}
                    onDelete={onDelete}
                />
            ))}
        </div>
    );
}

export default function Tag({ name, onDelete }) {
    const onRequestDelete = isFunction(onDelete) ? () => onDelete(name) : undefined;

    return (
        <Chip
            style={tagStyle}
            onRequestDelete={onRequestDelete}
            children={name}
        />
    );
}

export function TagFilter({ dataSource = [], activeFilter = [], addFilter, removeFilter }) {
    return (
        <div style={{ ...flexWrapperStyle, alignItems: 'baseline' }}>
            <AutoComplete
                style={{ width: 150 }}
                fullWidth={true}
                dataSource={dataSource}
                dataSourceConfig={{ text: 'name', value: 'name' }}
                onNewRequest={addFilter}
                floatingLabelText={<span>Filter Groups</span>}
                hintText="Enter filter"
            />
            <Tags tags={activeFilter} onDelete={removeFilter} />
        </div>
    );
}
