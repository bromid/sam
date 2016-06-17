import React from 'react';
import Drawer from 'material-ui/Drawer';
import MenuItem from 'material-ui/MenuItem';
import { CardMedia } from 'material-ui/Card';
import { Link } from 'react-router';
import atgLogo from '../images/atg_logo.png';

const imageWidth = 98;
const boxShadow = 'rgba(0, 0, 0, 0.117647) 0px 1px 6px, rgba(0, 0, 0, 0.117647) 0px 1px 4px';
const logoBackground = 'linear-gradient(180deg,#0266b0 0,#09428f)';

function MainMenu({ mdPlus, isOpen, setMenuOpen }) {
    return (
        <Drawer docked={mdPlus} open={isOpen} onRequestChange={setMenuOpen} width={200}>
            <CardMedia mediaStyle={{ padding: 27, textAlign: 'center', backgroundImage: logoBackground, boxShadow: boxShadow}}>
                <img src={atgLogo} style={{width: imageWidth, maxWidth: imageWidth, minWidth: imageWidth}} alt="ATG logo" />
            </CardMedia>
            <div style={{marginTop: 30}}>
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
            </div>
        </Drawer>
    );
}

export default MainMenu;
