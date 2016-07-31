import path from 'path';
import webpack from 'webpack';
import CopyWebpackPlugin from 'copy-webpack-plugin';

const Paths = {
    SRC: path.resolve('src'),
    APP_ENTRY: path.resolve('src/index'),
    ICONS: path.resolve('src/icons'),
    DIST: path.resolve('dist/static'),
    PUBLIC: '/static/',
};

export default {
    devtool: 'cheap-source-map',
    entry: Paths.APP_ENTRY,
    output: {
        path: Paths.DIST,
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
