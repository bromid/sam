import { combineReducers } from 'redux';
import createCRUDReducers from './helpers/createCRUDReducers';

const { CRUDReducers, CRUDSelectors } = createCRUDReducers('SERVER');

export const fromServer = {
    ...CRUDSelectors,
};

export default combineReducers({
    ...CRUDReducers,
});
