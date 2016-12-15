import createItemValidator from './helpers/createItemValidator';
import { containsWhitespace } from '../helpers';

/*
 ^                     # Start of string
 (?=.{1,50}$)          # Assert length of string: 1-50 characters
 (                     # Match the following group (domain name segment):
 (?=[a-z0-9-]{1,50}\.) # Assert length of group: 1-50 characters
 (xn--+)?              # Allow punycode notation (at least two dashes)
 [a-z0-9]+             # Match letters/digits
 (-[a-z0-9]+)*         # optionally followed by dash-separated letters/digits
 \.                    # followed by a dot.
 )+                    # Repeat this as needed (at least one match is required)
 [a-z]{2,63}           # Match the TLD (at least 2 characters)
 $                     # End of string
 */
const fqdnRegex =
    /^(?=.{1,50}$)((?=[a-z0-9-]{1,50}\.)(xn--+)?[a-z0-9]+(-[a-z0-9]+)*\.)+[a-z]{2,63}$/i;

export const hostname = (param, fieldName = 'Hostname') => {
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

export const environment = (param, fieldName = 'Environment') => {
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

export const fqdn = (param, fieldName = 'Qualified domain name') => {
    const length = param.length;

    if (length < 1) {
        return `${fieldName} is mandatory`;
    }
    if (length < 2 || length > 500) {
        return `${fieldName} must be between 2 and 500 characters`;
    }
    if (containsWhitespace(param)) {
        return `${fieldName} should not contain whitespace characters`;
    }
    if (!fqdnRegex.test(param)) {
        return `${fieldName} is not a valid FQDN format (i.e. hostname.test.hh.atg.se)`;
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

export const server = (param) => createItemValidator([
    ['hostname', hostname(param.hostname)],
    ['environment', environment(param.environment)],
    ['fqdn', fqdn(param.fqdn)],
    ['description', description(param.description)],
]);
