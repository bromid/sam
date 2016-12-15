import React from 'react';
import Drawer from 'material-ui/Drawer';
import MenuItem from 'material-ui/MenuItem';
import { CardMedia } from 'material-ui/Card';
import { Link } from 'react-router';
import Version from './Version';

const boxShadow = 'rgba(0, 0, 0, 0.117647) 0px 1px 6px, rgba(0, 0, 0, 0.117647) 0px 1px 4px';
const logoBackground = 'linear-gradient(180deg,#0266b0 0,#09428f)';

function MainMenu({ docked, isOpen, setMenuOpen, closeMenu }) {
    const mediaStyle = {
        minHeight: 46,
        padding: 27,
        textAlign: 'center',
        backgroundImage: logoBackground,
        boxShadow,
    };

    return (
        <Drawer docked={docked} open={isOpen} onRequestChange={setMenuOpen} width={200}>
            <div style={{ display: 'flex', flexDirection: 'column', height: '100%' }}>
                <CardMedia mediaStyle={mediaStyle} />
                <div style={{ flex: 1, marginTop: 30 }}>
                    <Link onClick={closeMenu} to="/group">
                        <MenuItem>Groups</MenuItem>
                    </Link>
                    <Link onClick={closeMenu} to="/application">
                        <MenuItem>Applications</MenuItem>
                    </Link>
                    <Link onClick={closeMenu} to="/server">
                        <MenuItem>Servers</MenuItem>
                    </Link>
                    <Link onClick={closeMenu} to="/asset">
                        <MenuItem>Assets</MenuItem>
                    </Link>
                </div>
                <Version />
            </div>
        </Drawer>
    );
}
export default MainMenu;
