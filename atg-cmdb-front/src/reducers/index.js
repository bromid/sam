import { combineReducers } from 'redux';
import group from './group';
import application from './application';
import server from './server';
import asset from './asset';
import menuOpen from './menu';
import metaOpen from './meta';
import searchResults from './search';
import info from './info';
import releaseNotes from './releaseNotes';

export default combineReducers({
    ...group,
    ...application,
    ...server,
    ...asset,
    menuOpen,
    metaOpen,
    ...searchResults,
    ...info,
    ...releaseNotes,
});
