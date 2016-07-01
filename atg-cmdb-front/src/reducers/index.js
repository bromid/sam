import { combineReducers } from 'redux';
import { groups, groupsIsLoading } from './groups';
import { applications, applicationsIsLoading } from './applications';
import { servers, serversIsLoading } from './servers';
import { menuOpen } from './menu';
import {searchResults, searchIsLoading} from './search';

const cmdbState = combineReducers({
    groups,
    groupsIsLoading,
    applications,
    applicationsIsLoading,
    servers,
    serversIsLoading,
    menuOpen,
    searchResults,
    searchIsLoading
});

export default cmdbState;
