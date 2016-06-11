import { combineReducers } from 'redux';
import { groups, groupsIsLoading } from './groups';
import { applications, applicationsIsLoading } from './applications';
import { menuOpen } from './menu';

const cmdbState = combineReducers({
    groups,
    groupsIsLoading,
    applications,
    applicationsIsLoading,
    menuOpen,
});

export default cmdbState;
