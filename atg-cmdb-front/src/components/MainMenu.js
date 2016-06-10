import React from 'react';
import Drawer from 'material-ui/Drawer';
import MenuItem from 'material-ui/MenuItem';

function MainMenu({ mdPlus, isOpen, setMenuOpen }) {
    return (
        <Drawer docked={mdPlus} open={isOpen} onRequestChange={setMenuOpen}>
            <MenuItem>Grupper</MenuItem>
            <MenuItem>Applikationer</MenuItem>
            <MenuItem>Servrar</MenuItem>
            <MenuItem>Tillgångar</MenuItem>
        </Drawer>
    );
}

export default MainMenu;
