import path from 'path';
import webpack from 'webpack';
import HtmlWebpackPlugin from 'html-webpack-plugin';
import CopyWebpackPlugin from 'copy-webpack-plugin';
import validate from 'webpack-validator';
import envVarConfig from './dev-env-vars';

const Paths = {
    SRC: path.resolve('src'),
    APP_ENTRY: ['webpack-hot-middleware/client', path.resolve('src/index')],
    ICONS: path.resolve('src/icons'),
    DIST: path.resolve('dist/static'),
    PUBLIC: '/static/',
};

const config = {
    devtool: 'eval',
    entry: Paths.APP_ENTRY,
    output: {
        path: Paths.DIST,
        filename: 'bundle.js',
        publicPath: Paths.PUBLIC,
    },
    plugins: [
        new HtmlWebpackPlugin({
            template: 'index.mustache',
            env: envVarConfig,
        }),
        new CopyWebpackPlugin([
            {
                from: Paths.ICONS,
                to: 'icons',
            },
        ]),
        new webpack.HotModuleReplacementPlugin(),
        new webpack.NoErrorsPlugin(),
        new webpack.ContextReplacementPlugin(/moment[\\\/]locale$/, /^\.\/(en)$/),
    ],
    module: {
        loaders: [
            {
                test: /\.mustache/,
                loaders: ['mustache'],
            },
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
