import { OPEN_MENU, CLOSE_MENU, ENTER_DASHBOARD_MODE, EXIT_DASHBOARD_MODE } from '../constants';
import { getIsMenuOpen } from '../reducers';

export const openMenu = () => ({ type: OPEN_MENU });

export const closeMenu = () => ({ type: CLOSE_MENU });

export const toggleMenu = () => (dispatch, getState) => {
    const menuOpen = getIsMenuOpen(getState());
    dispatch(menuOpen ? closeMenu() : openMenu());
};

export const setMenuOpen = (open) => (open ? openMenu() : closeMenu());

export const enterDashboardMode = () => ({ type: ENTER_DASHBOARD_MODE });

export const exitDashboardMode = () => ({ type: EXIT_DASHBOARD_MODE });
