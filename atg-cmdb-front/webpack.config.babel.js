import path from 'path';
import CopyWebpackPlugin from 'copy-webpack-plugin';

const Paths = {
    src: path.join(__dirname, 'src'),
    public: '/static/',
};

export default {
    devtool: 'eval',
    entry: './src/index',
    output: {
        path: path.join(__dirname, 'dist/static'),
        filename: 'bundle.js',
        publicPath: Paths.public,
    },
    plugins: [
        new CopyWebpackPlugin([
            {
                from: path.join(Paths.src, 'icons'),
                to: 'icons',
            },
        ]),
    ],
    module: {
        loaders: [
            {
                test: /\.js$/,
                loaders: ['babel'],
                include: Paths.src,
            }, {
                test: /\.json$/,
                loaders: ['json'],
            }, {
                test: /\.(png|jpg)$/,
                include: Paths.src,
                loader: 'url?name=img/[name]-[hash].[ext]&limit=25000',
            },
        ],
    },
};
