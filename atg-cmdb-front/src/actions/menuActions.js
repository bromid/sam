import { OPEN_MENU, CLOSE_MENU } from '../constants';
import { getIsMenuOpen } from '../reducers';

export const openMenu = () => ({ type: OPEN_MENU });

export const closeMenu = () => ({ type: CLOSE_MENU });

export const toggleMenu = () => (dispatch, getState) => {
    const menuOpen = getIsMenuOpen(getState());
    dispatch(menuOpen ? closeMenu() : openMenu());
};

export const setMenuOpen = (open) => (open ? openMenu() : closeMenu());
