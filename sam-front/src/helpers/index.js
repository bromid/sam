import isArray from 'lodash/isArray';
import isArrayLike from 'lodash/isArrayLike';
import isObjectLike from 'lodash/isObjectLike';
import isEmpty from 'lodash/isEmpty';
import pickBy from 'lodash/pickBy';
import size from 'lodash/size';

export const toArray = (item) => (
    isArray(item) ? item : [item]
);

export const empty = (value) => {
    if (value === undefined || value === null) return true;
    if (isArrayLike(value) || isObjectLike(value)) return isEmpty(value);
    return false;
};

export const removeEmptyFields = (obj) => (
    pickBy(obj, (value) => !empty(value))
);

export const containsWhitespace = (val) => (
    /\s/.test(val)
);

export const collectionSize = (collection, defaultValue = '(0)') => {
    if (!collection) return defaultValue;
    return `(${size(collection)})`;
};
