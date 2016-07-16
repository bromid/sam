import { combineReducers } from 'redux';
import groupList from './groups';
import group from './group';
import groupTags from './groupTags';
import applicationList from './applications';
import application from './application';
import applicationDeployments from './applicationDeployments';
import serverList from './servers';
import server from './server';
import assetList from './assets';
import asset from './asset';
import { menuOpen } from './menu';
import { metaOpen } from './meta';
import searchResults from './search';
import info from './info';
import releaseNotes from './releaseNotes';

const cmdbState = combineReducers({
    ...groupList,
    ...group,
    ...groupTags,
    ...applicationList,
    ...application,
    ...applicationDeployments,
    ...serverList,
    ...server,
    ...assetList,
    ...asset,
    menuOpen,
    metaOpen,
    ...searchResults,
    ...info,
    ...releaseNotes,
});

export default cmdbState;
