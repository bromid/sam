
export const State = {
    readonly: 'readonly',
    editable: 'editable',
    editing: 'editing',
    saving: 'saving',
    saveFailed: 'saveFailed',
    validationFailed: 'validationFailed',
};

export const AllStates = [
    State.readonly,
    State.editable,
    State.editing,
    State.saving,
    State.saveFailed,
    State.validationFailed,
];

export const isEditState = (id) => {
    switch (id) {
        case State.editing:
        case State.saving:
        case State.saveFailed:
        case State.validationFailed:
            return true;
        default:
            return false;
    }
};

export const isShowEditForm = (id) => {
    switch (id) {
        case State.editing:
        case State.validationFailed:
            return true;
        default:
            return false;
    }
};
