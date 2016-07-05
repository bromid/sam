import { combineReducers } from 'redux';
import { groupList, groupListIsLoading } from './groups';
import { applicationList, applicationListIsLoading } from './applications';
import { serverList, serverListIsLoading } from './servers';
import { server, serverIsLoading } from './server';
import { menuOpen } from './menu';
import { searchResults, searchIsLoading } from './search';
import { info, infoIsLoading } from './info';
import { releaseNotes, releaseNotesIsLoading } from './releaseNotes';

const cmdbState = combineReducers({
    groupList,
    groupListIsLoading,
    applicationList,
    applicationListIsLoading,
    serverList,
    serverListIsLoading,
    server,
    serverIsLoading,
    menuOpen,
    searchResults,
    searchIsLoading,
    info,
    infoIsLoading,
    releaseNotes,
    releaseNotesIsLoading,
});

export default cmdbState;
