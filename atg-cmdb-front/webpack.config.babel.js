import path from 'path';

const srcPath = path.join(__dirname, 'src');
const iconsPath = path.join(srcPath, 'icons');

var publicPath = "/static/";

export default {
    devtool: 'eval',
    entry: './src/index',
    output: {
        path: path.join(__dirname, 'dist/static'),
        filename: 'bundle.js',
        publicPath: publicPath
    },
    module: {
        loaders: [
            {
                test: /\.js$/,
                loaders: ['babel'],
                include: srcPath,
            }, {
                test: /\.json$/,
                loaders: ['json'],
                include: srcPath,
            }, {
                include: iconsPath,
                loader: 'file?name=icons/[name].[ext]'
            }, {
                test: /\.(png|jpg)$/,
                include: srcPath,
                exclude: /\/icons\//,
                loader: 'url?name=img/[name]-[hash].[ext]&limit=25000',
            }
        ],
    },
};
