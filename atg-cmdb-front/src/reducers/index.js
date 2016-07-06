import { combineReducers } from 'redux';
import { groupList, groupListIsLoading } from './groups';
import { group, groupIsLoading } from './group';
import { applicationList, applicationListIsLoading } from './applications';
import { application, applicationIsLoading } from './application';
import { serverList, serverListIsLoading } from './servers';
import { server, serverIsLoading } from './server';
import { assetList, assetListIsLoading } from './assets';
import { asset, assetIsLoading } from './asset';
import { menuOpen } from './menu';
import { searchResults, searchIsLoading } from './search';
import { info, infoIsLoading } from './info';
import { releaseNotes, releaseNotesIsLoading } from './releaseNotes';

const cmdbState = combineReducers({
    groupList,
    groupListIsLoading,
    group,
    groupIsLoading,
    applicationList,
    applicationListIsLoading,
    application,
    applicationIsLoading,
    serverList,
    serverListIsLoading,
    server,
    serverIsLoading,
    assetList,
    assetListIsLoading,
    asset,
    assetIsLoading,
    menuOpen,
    searchResults,
    searchIsLoading,
    info,
    infoIsLoading,
    releaseNotes,
    releaseNotesIsLoading,
});

export default cmdbState;
