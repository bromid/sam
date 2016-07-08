import React from 'react';
import ReactMarkdown from 'react-markdown';
import { Tabs, Tab } from 'material-ui/Tabs';
import Chip from 'material-ui/Chip';
import Meta from './Meta';

const flexWrapper = {
    display: 'flex',
    flexFlow: 'row wrap',
    justifyContent: 'flex-start',
    alignItems: 'stretch',
};

const tagStyle = {
    margin: '0.5em 0.5em 0 0',
};

const containerStyle = {
    flex: 1,
    margin: 10,
};

function deleteTag() {
}

function Description({ description }) {
    if (!description) return <div style={containerStyle} />;
    return (
        <ReactMarkdown
            skipHtml={true}
            containerProps={{ style: containerStyle }}
            source={description}
        />
    );
}

function Tags({ tags }) {
    if (!tags) return null;
    return (
        <div style={{ ...flexWrapper, height: 40 }}>
            {tags.map(tag => (
                <Chip
                    key={tag.name}
                    style={tagStyle}
                    onRequestDelete={deleteTag}
                    children={tag.name}
                />
            ))}
        </div>
    );
}

export default function ItemView({ headline, description, meta, tabs, tags }) {
    return (
        <div>
            <h2>{headline}</h2>
            <Tags tags={tags} />
            <div style={{ ...flexWrapper, minHeight: 20 }}>
                <Description description={description} />
                <Meta meta={meta} />
            </div>
            <Tabs>
                {tabs.map(tab => (
                    <Tab key={tab.name} label={tab.name}>
                        <div style={containerStyle}>
                            {tab.node}
                        </div>
                    </Tab>
                ))}
            </Tabs>
        </div>
    );
}
