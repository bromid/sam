import path from 'path';
import CopyWebpackPlugin from 'copy-webpack-plugin';
import validate from 'webpack-validator';

const Paths = {
    SRC: path.resolve('src'),
    APP_ENTRY: path.resolve('src/index'),
    ICONS: path.resolve('src/icons'),
    DIST: path.resolve('dist/static'),
    PUBLIC: '/static/',
};

const config = {
    devtool: 'eval',
    entry: Paths.APP_ENTRY,
    output: {
        path: path.join(__dirname, 'dist/static'),
        filename: 'bundle.js',
        publicPath: Paths.PUBLIC,
    },
    plugins: [
        new CopyWebpackPlugin([
            {
                from: Paths.ICONS,
                to: 'icons',
            },
        ]),
    ],
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
