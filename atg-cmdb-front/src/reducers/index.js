import { combineReducers } from 'redux';
import { groups, groupsIsLoading } from './groups';
import { menuOpen } from './menu';

const cmdbState = combineReducers({
    groups,
    groupsIsLoading,
    menuOpen,
});

export default cmdbState;
