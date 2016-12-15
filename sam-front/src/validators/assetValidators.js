import createItemValidator from './helpers/createItemValidator';
import { containsWhitespace } from '../helpers';
import * as groupValidators from './groupValidators';

export const id = (param) => {
    const length = param.length;

    if (length < 1) {
        return 'Id is mandatory';
    }
    if (length < 2 || length > 50) {
        return 'Id must be between 2 and 50 characters';
    }
    if (containsWhitespace(param)) {
        return 'Id should not contain whitespace characters';
    }
    return '';
};

export const name = (param) => {
    const length = param.length;

    if (length < 1) {
        return 'Name is mandatory';
    }
    if (length < 3 || length > 50) {
        return 'Name must be between 3 and 50 characters';
    }
    return '';
};

export const description = (param) => {
    const length = param.length;

    const MAX_LENGTH = 1500;
    if (length > MAX_LENGTH) {
        return `Description must be shorter than ${MAX_LENGTH} characters`;
    }
    return '';
};

export const group = (param) => {
    const length = param.length;

    if (length > 0) {
        return groupValidators.id(param, 'Group');
    }
    return '';
};

export const asset = (param) => createItemValidator([
    ['id', id(param.id)],
    ['name', name(param.name)],
    ['group', group(param.group)],
    ['description', description(param.description)],
]);
