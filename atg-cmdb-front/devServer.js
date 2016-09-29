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
    contentBase: path.join(__dirname, '/dist'),
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
    const filename = path.join(__dirname, '/dist/static/index.html');
    compiler.outputFileSystem.readFile(filename, (err, result) => {
        res.set('content-type', 'text/html');
        res.send((err) ? 'Bundle not finished' : result);
        res.end();
    });
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
