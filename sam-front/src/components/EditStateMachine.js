import isFunction from 'lodash/isFunction';
import first from 'lodash/first';
import keys from 'lodash/keys';
import { State, isEditState } from './EditState';

export const parseError = (error) => {
    if (error && error.error) {
        const body = JSON.parse(error.options.body);
        const fieldName = first(keys(body));
        return {
            isError: true,
            message: error.message,
            value: body[fieldName],
        };
    }
    return { isError: false };
};

export const mapState = (isAuthenticated, editFunction, editing = false, disable = false) => {
    if (disable) return State.readonly;
    if (editing) return State.editing;
    if (isAuthenticated && isFunction(editFunction)) return State.editable;
    return State.readonly;
};

export const newPropsState = (name, ownState, otherState, value, error) => {
    const stateName = `${name}State`;
    const errorName = `${name}ErrorText`;
    if (isEditState(otherState)) {
        return {
            [name]: value,
            [stateName]: 'readonly',
        };
    }
    if (ownState === State.saveFailed) {
        return {
            [name]: error.value,
            [stateName]: ownState,
            [errorName]: error.message,
        };
    }
    return {
        [name]: value,
        [stateName]: ownState,
    };
};

export const changeState = (name, value, errorText) => {
    const stateName = `${name}State`;
    const errorName = `${name}ErrorText`;
    const newState = (errorText.length > 1) ? State.validationFailed : State.editing;
    return {
        [name]: value,
        [stateName]: newState,
        [errorName]: errorText,
    };
};

export const mapStateFromCurrent = (currentState, isAuthenticated, editFunction, saving, error) => {
    if (currentState === State.saving || currentState === State.saveFailed) {
        if (error) return State.saveFailed;
    }
    if (currentState === State.editing) {
        if (saving) return State.saving;
        return State.editing;
    }
    return mapState(isAuthenticated, editFunction);
};
