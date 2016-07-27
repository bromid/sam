import isArray from 'lodash/isArray';

export const toArray = (item) => (
    isArray(item) ? item : [item]
);
