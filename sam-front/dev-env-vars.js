const config = JSON.stringify({
    oauth: {
        url: 'http://localhost:3001/login/oauth/authorize',
        clientId: '123',
        origin: 'http://localhost:3001',
        scopes: 'openid'
    },
}, null, '  ');
export default config;
