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
        // Only include english locale for moment
        new webpack.ContextReplacementPlugin(/moment[\\\/]locale$/, /^\.\/(en)$/),
        // Exclude ACE editor and AJV from jsoneditor since they are not needed
        new webpack.IgnorePlugin(/(ajv|brace)$/, /jsoneditor/),
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
                test: /\.(png|jpg|svg)$/,
                loader: 'url?name=img/[name]-[hash].[ext]&limit=25000',
            }, {
                test: /\.css$/,
                loader: 'style?insertAt=top&singleton!css',
            },
        ],
    },
};
export default validate(config);
