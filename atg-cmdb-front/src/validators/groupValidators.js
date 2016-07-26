import createItemValidator from './helpers/createItemValidator';

export const id = (param) => {
    const length = param.length;

    if (length < 1) {
        return 'Id is mandatory';
    }
    if (length < 2 || length > 50) {
        return 'Id must be between 2 and 50';
    }
    return '';
};

export const name = (param) => {
    const length = param.length;

    if (length < 1) {
        return 'Name is mandatory';
    }
    if (length < 3 || length > 50) {
        return 'Name must be between 3 and 50';
    }
    return '';
};

export const description = (param) => {
    const length = param.length;

    if (length > 1000) {
        return 'Description must be shorter than 1000.';
    }
    return '';
};

export const group = (param) => createItemValidator([
    ['id', id(param.id)],
    ['name', name(param.name)],
    ['description', description(param.description)],
]);
