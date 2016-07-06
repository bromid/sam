import { combineReducers } from 'redux';
import groupList from './groups';
import group from './group';
import applicationList from './applications';
import application from './application';
import serverList from './servers';
import server from './server';
import assetList from './assets';
import asset from './asset';
import { menuOpen } from './menu';
import searchResults from './search';
import info from './info';
import releaseNotes from './releaseNotes';

const cmdbState = combineReducers({
    ...groupList,
    ...group,
    ...applicationList,
    ...application,
    ...serverList,
    ...server,
    ...assetList,
    ...asset,
    menuOpen,
    ...searchResults,
    ...info,
    ...releaseNotes,
});

export default cmdbState;
