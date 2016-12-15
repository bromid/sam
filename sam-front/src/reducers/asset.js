import { combineReducers } from 'redux';
import createCRUDReducers from './helpers/createCRUDReducers';

const { CRUDReducers, CRUDSelectors } = createCRUDReducers('ASSET');

export const fromAsset = {
    ...CRUDSelectors,
};

export default combineReducers({
    ...CRUDReducers,
});
