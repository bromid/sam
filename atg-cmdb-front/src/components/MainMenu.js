import React from 'react';
import Drawer from 'material-ui/Drawer';
import MenuItem from 'material-ui/MenuItem';
import { CardMedia } from 'material-ui/Card';
import atgLogo from '../images/atg_logo.png';

function MainMenu({ mdPlus, isOpen, setMenuOpen }) {
    return (
        <Drawer docked={mdPlus} open={isOpen} onRequestChange={setMenuOpen}>
            <CardMedia mediaStyle={{ margin: '34px' }}>
                <img src={atgLogo} alt="ATG logo" />
            </CardMedia>
            <MenuItem>Grupper</MenuItem>
            <MenuItem>Applikationer</MenuItem>
            <MenuItem>Servrar</MenuItem>
            <MenuItem>Tillgångar</MenuItem>
        </Drawer>
    );
}

export default MainMenu;
