import path from 'path';
import webpack from 'webpack';
import HtmlWebpackPlugin from 'html-webpack-plugin';
import CopyWebpackPlugin from 'copy-webpack-plugin';
import validate from 'webpack-validator';

process.env.NODE_ENV = 'production';

const Paths = {
    SRC: path.resolve('src'),
    APP_ENTRY: path.resolve('src/index'),
    ICONS: path.resolve('src/icons'),
    DIST: path.resolve('dist/static'),
    PUBLIC: '/static/',
};

const config = {
    devtool: 'cheap-source-map',
    entry: Paths.APP_ENTRY,
    output: {
        path: Paths.DIST,
        filename: '[name].[chunkhash].js',
        publicPath: Paths.PUBLIC,
    },
    plugins: [
        new HtmlWebpackPlugin({
            minify: {
                removeComments: true,
                collapseWhitespace: true,
                minifyJS: true,
            },
            filename: 'index.mustache',
            template: 'index.mustache',
        }),
        new CopyWebpackPlugin([
            {
                from: Paths.ICONS,
                to: 'icons',
            },
        ]),
        new webpack.ContextReplacementPlugin(/moment[\\\/]locale$/, /^\.\/(en)$/),
        new webpack.DefinePlugin({ 'process.env.NODE_ENV': '"production"' }),
    ],
    stats: {
        hash: false,
        version: true,
        timings: true,
        assets: true,
        chunks: false,
        modules: false,
        reasons: false,
        children: false,
        source: false,
        errors: true,
        errorDetails: true,
        warnings: false,
        publicPath: false,
    },
    module: {
        loaders: [
            {
                test: /\.js$/,
                loaders: ['babel'],
                include: Paths.SRC,
            }, {
                test: /\.json$/,
                loaders: ['json'],
            }, {
                test: /\.(png|jpg)$/,
                include: Paths.SRC,
                loader: 'url?name=img/[name]-[hash].[ext]&limit=25000',
            },
        ],
    },
};
export default validate(config);
