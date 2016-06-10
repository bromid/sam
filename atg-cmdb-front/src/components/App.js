import React from 'react';
import Groups from './Groups';
import getMuiTheme from 'material-ui/styles/getMuiTheme';
import MuiThemeProvider from 'material-ui/styles/MuiThemeProvider';
import AppBar from 'material-ui/AppBar';

const App = () => (
    <MuiThemeProvider muiTheme={getMuiTheme()}>
        <div>
            <AppBar title="Assets Management Dashboard" showMenuIconButton={false} />
            <Groups />
        </div>
    </MuiThemeProvider>
);

export default App;
