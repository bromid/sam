import path from 'path';
import webpack from 'webpack';
import webpackDevMiddleware from 'webpack-dev-middleware';
import config from './webpack.config.babel';
import Express from 'express';
import httpProxy from 'http-proxy';
import DashboardPlugin from 'webpack-dashboard/plugin';

const app = new Express();
const servicesProxy = httpProxy.createProxyServer();
const port = 3001;

const compiler = webpack(config);
compiler.apply(new DashboardPlugin());

app.use(webpackDevMiddleware(compiler, {
    stats: {
        colors: true,
        chunks: false,
        assets: false,
    },
    publicPath: config.output.publicPath,
}));

app.use('/services/*', (req, res) => {
    req.url = req._parsedUrl.path; // eslint-disable-line
    servicesProxy.web(req, res, {
        target: {
            port: 8080,
            host: 'localhost',
        },
    });
});

app.get('/*', (req, res) => {
    res.sendFile(path.join(__dirname, 'index.html'));
});

app.listen(port, (error) => {
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
