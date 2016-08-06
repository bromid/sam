import isEmpty from 'lodash/isEmpty';

export default function createItemValidator(errors) {
    let first = null;
    const text = {};

    for (const [key, value] of errors) {
        if (value) {
            if (!first) {
                first = key;
            }
            text[`${key}ErrorText`] = value;
        }
    }

    return {
        first,
        text,
        hasError: !isEmpty(text),
    };
}
