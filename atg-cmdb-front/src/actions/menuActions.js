import { OPEN_MENU, CLOSE_MENU } from '../constants';

export const openMenu = () => ({ type: OPEN_MENU });

export const closeMenu = () => ({ type: CLOSE_MENU });

export const toggleMenu = () => (dispatch, getState) => {
    const { menuOpen } = getState();
    dispatch(menuOpen ? closeMenu() : openMenu());
};

export const setMenuOpen = (open) => {
    return open ? openMenu() : closeMenu();
};
