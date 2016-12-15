import { combineReducers } from 'redux';
import { OPEN_MENU, CLOSE_MENU, ENTER_DASHBOARD_MODE, EXIT_DASHBOARD_MODE } from '../constants';

const menuOpen = (state = false, action) => {
    switch (action.type) {
        case OPEN_MENU:
            return true;
        case CLOSE_MENU:
            return false;
        default:
            return state;
    }
};

const dashboardMode = (state = false, action) => {
    switch (action.type) {
        case ENTER_DASHBOARD_MODE:
            return true;
        case EXIT_DASHBOARD_MODE:
            return false;
        default:
            return state;
    }
};

export const fromMenu = {
    getIsOpen: (state) => state.menuOpen,
    getIsDashboardMode: (state) => state.dashboardMode,
};

export default combineReducers({
    menuOpen,
    dashboardMode,
});
