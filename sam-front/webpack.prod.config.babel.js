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
        // Only include english locale for moment
        new webpack.ContextReplacementPlugin(/moment[\\\/]locale$/, /^\.\/(en)$/),
        // Exclude ACE editor and AJV from jsoneditor since they are not needed
        new webpack.IgnorePlugin(/(ajv|brace)$/, /jsoneditor/),
        // Set production environment
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
