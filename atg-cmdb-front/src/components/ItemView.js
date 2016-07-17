import React from 'react';
import ReactMarkdown from 'react-markdown';
import { Tabs, Tab } from 'material-ui/Tabs';
import Meta from './Meta';
import { Tags } from './Tag';

const flexWrapper = {
    display: 'flex',
    flexFlow: 'row wrap',
    justifyContent: 'flex-start',
    alignItems: 'flex-start',
};

const containerStyle = {
    flex: 1,
    margin: 10,
};

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

export default function ItemView(props) {
    const {
        headline, description, tags,
        meta, metaOpen, toggleMeta,
        tabs, selectedTab, onTabChanged,
    } = props;
    return (
        <div>
            <h2>{headline}</h2>
            <Tags tags={tags} />
            <div style={{ ...flexWrapper, minHeight: 20 }}>
                <Description description={description} />
                <Meta meta={meta} open={metaOpen} toggle={toggleMeta} />
            </div>
            <Tabs value={selectedTab} onChange={onTabChanged} >
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
}
