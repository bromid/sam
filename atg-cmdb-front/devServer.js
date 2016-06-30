import path from 'path';
import webpack from 'webpack';
import webpackDevMiddleware from 'webpack-dev-middleware';
import config from './webpack.config.babel';
import Express from 'express';
import httpProxy from 'http-proxy';

const app = new Express();
const servicesProxy = httpProxy.createProxyServer();
const port = 3001;

const compiler = webpack(config);
app.use(webpackDevMiddleware(compiler, {
    noInfo: true,
    publicPath: config.output.publicPath
}));

app.use("/services/*", function(req, res) {
    req.url = req.baseUrl;
    servicesProxy.web(req, res, {
        target: {
            port: 8080,
            host: 'localhost'
        }
    });
});

app.get('/*', (req, res) => {
    res.sendFile(path.join(__dirname, 'index.html'));
});

app.listen(port, error => {
    /* eslint-disable no-console */
    if (error) {
        console.error(error);
    } else {
        console.info(
            'Listening on port %s. Open up http://localhost:%s/ in your browser.',
            port,
            port
        );
    }
    /* eslint-enable no-console */
});
