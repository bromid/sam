import React from 'react';
import Chip from 'material-ui/Chip';
import AutoComplete from 'material-ui/AutoComplete';
import isObject from 'lodash/isObject';

const flexWrapper = {
    display: 'flex',
    flexFlow: 'row wrap',
    justifyContent: 'flex-start',
    alignItems: 'stretch',
};

const tagStyle = {
    margin: '0.5em 0.5em 0 0',
};

function deleteTag() {}

function getTagName(tag) {
    return isObject(tag) ? tag.name : tag;
}

export function Tags({ tags, onDelete, getName = getTagName }) {
    if (!tags) return null;

    tags = [].concat(tags); // eslint-disable-line no-param-reassign

    return (
        <div style={{ ...flexWrapper, height: 40 }}>
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

export default function Tag({ name, onDelete = deleteTag }) {
    return (
        <Chip
            style={tagStyle}
            onRequestDelete={() => onDelete(name)}
            children={name}
        />
    );
}

export function TagFilter({ dataSource = [], activeFilter = [], addFilter, removeFilter }) {
    return (
        <div style={flexWrapper}>
            <AutoComplete
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
