import { combineReducers } from 'redux';
import mapValues from 'lodash/mapValues';
import group, { fromGroup as _fromGroup } from './group';
import application, { fromApplication as _fromApplication } from './application';
import server, { fromServer as _fromServer } from './server';
import asset, { fromAsset as _fromAsset } from './asset';
import menuOpen from './menu';
import metaOpen from './meta';
import searchResults, { fromSearchResults as _fromSearchResults } from './search';
import info, { fromInfo as _fromInfo } from './info';
import releaseNotes, { fromReleaseNotes as _fromReleaseNotes } from './releaseNotes';
import auth, { fromAuth as _fromAuth } from './auth';
import notification from './notification';

export default combineReducers({
    group,
    application,
    server,
    asset,
    menuOpen,
    metaOpen,
    auth,
    notification,
    searchResults,
    info,
    releaseNotes,
});

const supplyStateSlice = (selectors, slice) =>
    mapValues(selectors, (selector) => (state) => selector(state[slice]));

export const fromGroup = supplyStateSlice(_fromGroup, 'group');
export const fromApplication = supplyStateSlice(_fromApplication, 'application');
export const fromServer = supplyStateSlice(_fromServer, 'server');
export const fromAsset = supplyStateSlice(_fromAsset, 'asset');

export const fromSearchResults = supplyStateSlice(_fromSearchResults, 'searchResults');
export const fromInfo = supplyStateSlice(_fromInfo, 'info');
export const fromReleaseNotes = supplyStateSlice(_fromReleaseNotes, 'releaseNotes');

export const fromAuth = supplyStateSlice(_fromAuth, 'auth');

export const getIsMenuOpen = (state) => state.menuOpen;
export const getIsMetaOpen = (state) => state.metaOpen;
export const getNotification = (state) => state.notification;
