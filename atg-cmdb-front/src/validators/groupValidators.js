import createItemValidator from './helpers/createItemValidator';
import { containsWhitespace } from '../helpers';

export const id = (param, fieldName = 'Id') => {
    const length = param.length;

    if (length < 1) {
        return `${fieldName} is mandatory`;
    }
    if (length < 2 || length > 50) {
        return `${fieldName} must be between 2 and 50 characters`;
    }
    if (containsWhitespace(param)) {
        return `${fieldName} should not contain whitespace characters`;
    }
    return '';
};

export const name = (param, fieldName = 'Name') => {
    const length = param.length;

    if (length < 1) {
        return `${fieldName} is mandatory`;
    }
    if (length < 3 || length > 50) {
        return `${fieldName} must be between 3 and 50 characters`;
    }
    return '';
};

export const description = (param) => {
    const length = param.length;

    if (length > 1000) {
        return 'Description must be shorter than 1000 characters';
    }
    return '';
};

export const group = (param) => createItemValidator([
    ['id', id(param.id)],
    ['name', name(param.name)],
    ['description', description(param.description)],
]);
