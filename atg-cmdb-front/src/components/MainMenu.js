import React from 'react';
import Drawer from 'material-ui/Drawer';
import MenuItem from 'material-ui/MenuItem';
import { CardMedia } from 'material-ui/Card';
import { Link } from 'react-router';
import atgLogo from '../images/atg_logo.png';

function MainMenu({ mdPlus, isOpen, setMenuOpen }) {
    return (
        <Drawer docked={mdPlus} open={isOpen} onRequestChange={setMenuOpen}>
            <CardMedia mediaStyle={{ margin: '34px' }}>
                <img src={atgLogo} alt="ATG logo" />
            </CardMedia>
            <Link to="/groups">
                <MenuItem>Grupper</MenuItem>
            </Link>
            <Link to="/applications">
                <MenuItem>Applikationer</MenuItem>
            </Link>
            <Link to="/servers">
                <MenuItem>Servrar</MenuItem>
            </Link>
            <Link to="/assets">
                <MenuItem>Tillgångar</MenuItem>
            </Link>
        </Drawer>
    );
}

export default MainMenu;
