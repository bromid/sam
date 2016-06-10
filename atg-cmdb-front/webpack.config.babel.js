import path from 'path';

export default {
    devtool: 'eval',
    entry: './src/index',
    output: {
        path: path.join(__dirname, 'dist/static'),
        filename: 'bundle.js',
    },
    module: {
        loaders: [
            {
                test: /\.js$/,
                loaders: ['babel'],
                exclude: /node_modules/,
                include: __dirname,
            }, {
                test: /\.json$/,
                loaders: ['json'],
                exclude: /node_modules/,
                include: __dirname,
            }, {
                test: /\.(png|jpg)$/,
                include: __dirname,
                loader: 'url?name=img/[name]-[hash].[ext]&limit=25000',
            },
        ],
    },
};
