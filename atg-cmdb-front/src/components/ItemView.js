import React from 'react';
import ReactMarkdown from 'react-markdown';
import { Tabs, Tab } from 'material-ui/Tabs';
import Meta from './Meta';

const flexWrapper = {
    display: 'flex',
    flexFlow: 'row wrap',
    justifyContent: 'flex-start',
    alignItems: 'stretch',
};

function Description({ description }) {
    if (!description) return <div style={{ flex: 1 }} />;
    return (
        <ReactMarkdown
            skipHtml={true}
            containerProps={{ style: { flex: 1 } }}
            source={description}
        />
    );
}

export default function ItemView({ headline, description, meta, tabs }) {
    return (
        <div>
            <h2>{headline}</h2>
            <div style={{ ...flexWrapper, minHeight: 20 }}>
                <Description description={description} />
                <Meta meta={meta} />
            </div>
            <Tabs>
                {tabs.map(tab => (
                    <Tab key={tab.name} label={tab.name}>
                        {tab.node}
                    </Tab>
                ))}
            </Tabs>
        </div>
    );
}
