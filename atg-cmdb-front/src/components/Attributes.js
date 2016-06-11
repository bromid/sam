import React from 'react';
import isObject from 'lodash/isObject';

function getPaths(obj, prev = [], acc = []) {
    Object.keys(obj).forEach((key) => {
        const value = obj[key];
        const path = prev.concat(key);

        if (isObject(value)) {
            getPaths(value, path, acc);
        } else {
            acc.push({ path: path.join('.'), value }); // eslint-disable-line no-param-reassign
        }
    });

    return acc;
}

export default function Attributes({ attributes }) {
    return (
        <ul>
            {getPaths(attributes).map(({ path, value }) => <li>{path}: {value}</li>)}
        </ul>
    );
}
