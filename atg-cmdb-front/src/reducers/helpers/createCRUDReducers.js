import createFetchReducer from './createFetchReducer';

export function createCRUDSelectors(fromCurrent, fromList, fromPatchResult) {
    return {
        getCurrent: (state) => fromCurrent.getData(state.current),
        getCurrentIsPending: (state) => fromCurrent.getIsPending(state.current),
        getCurrentError: (state) => fromCurrent.getError(state.current),

        getList: (state) => fromList.getData(state.list).items,
        getListIsPending: (state) => fromList.getIsPending(state.list),
        getListError: (state) => fromList.getError(state.list),

        getPatchResult: (state) => fromPatchResult.getData(state.patchResult),
        getPatchResultIsPending: (state) => fromPatchResult.getIsPending(state.patchResult),
        getPatchResultError: (state) => fromPatchResult.getError(state.patchResult),
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

    const [patchResult, fromPatchResult] = createFetchReducer({
        requestKey: `PATCH_${uDomainTypeName}_REQUEST`,
        receiveKey: `PATCH_${uDomainTypeName}_RESPONSE`,
    });

    return {
        CRUDReducers: { current, list, patchResult },
        CRUDSelectors: createCRUDSelectors(fromCurrent, fromList, fromPatchResult),
    };
}
