import createFetchReducer from './createFetchReducer';

export function createCRUDSelectors(
    fromCurrent, fromList, fromCreateResult, fromPatchResult, fromDeleteResult
) {
    return {
        getCurrent: (state) => fromCurrent.getData(state.current),
        getCurrentIsPending: (state) => fromCurrent.getIsPending(state.current),
        getCurrentError: (state) => fromCurrent.getError(state.current),

        getList: (state) => fromList.getData(state.list).items,
        getListIsPending: (state) => fromList.getIsPending(state.list),
        getListError: (state) => fromList.getError(state.list),

        getCreateResult: (state) => fromPatchResult.getData(state.createResult),
        getCreateResultIsPending: (state) => fromPatchResult.getIsPending(state.createResult),
        getCreateResultError: (state) => fromPatchResult.getError(state.createResult),

        getPatchResult: (state) => fromPatchResult.getData(state.patchResult),
        getPatchResultIsPending: (state) => fromPatchResult.getIsPending(state.patchResult),
        getPatchResultError: (state) => fromPatchResult.getError(state.patchResult),

        getDeleteResult: (state) => fromDeleteResult.getData(state.deleteResult),
        getDeleteResultIsPending: (state) => fromDeleteResult.getIsPending(state.deleteResult),
        getDeleteResultError: (state) => fromDeleteResult.getError(state.deleteResult),
    };
}

export default function createCRUDReducers(domainTypeName) {
    const uDomainTypeName = domainTypeName.toUpperCase();

    const [current, fromCurrent] = createFetchReducer({
        requestKey: `FETCH_${uDomainTypeName}_REQUEST`,
        receiveKey: `FETCH_${uDomainTypeName}_RESPONSE`,
    });


    const [list, fromList] = createFetchReducer({
        requestKey: `FETCH_${uDomainTypeName}_LIST_REQUEST`,
        receiveKey: `FETCH_${uDomainTypeName}_LIST_RESPONSE`,
    });

    const [createResult, fromCreateResult] = createFetchReducer({
        requestKey: `CREATE_${uDomainTypeName}_REQUEST`,
        receiveKey: `CREATE_${uDomainTypeName}_RESPONSE`,
    });

    const [patchResult, fromPatchResult] = createFetchReducer({
        requestKey: `PATCH_${uDomainTypeName}_REQUEST`,
        receiveKey: `PATCH_${uDomainTypeName}_RESPONSE`,
    });

    const [deleteResult, fromDeleteResult] = createFetchReducer({
        requestKey: `DELETE_${uDomainTypeName}_REQUEST`,
        receiveKey: `DELETE_${uDomainTypeName}_RESPONSE`,
    });

    return {
        CRUDReducers: { current, list, createResult, patchResult, deleteResult },
        CRUDSelectors: createCRUDSelectors(
            fromCurrent, fromList, fromCreateResult, fromPatchResult, fromDeleteResult
        ),
    };
}
