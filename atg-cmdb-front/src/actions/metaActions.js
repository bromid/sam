import { OPEN_META, CLOSE_META } from '../constants';
import { getIsMetaOpen } from '../reducers';

export const openMeta = () => ({ type: OPEN_META });

export const closeMeta = () => ({ type: CLOSE_META });

export const toggleMeta = () => (dispatch, getState) => {
    const metaOpen = getIsMetaOpen(getState());
    dispatch(metaOpen ? closeMeta() : openMeta());
};

export const setMetaOpen = (open) => (open ? openMeta() : closeMeta());
