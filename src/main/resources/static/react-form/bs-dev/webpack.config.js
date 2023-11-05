const path = require('path');
const webpack = require('webpack');

module.exports = {
  entry: path.join(__dirname, "src", "main.js"),
  output: {
    path: path.resolve(__dirname, "..", "bs"),
    libraryTarget: 'umd',
    clean: true
  },
  module: {
    rules: [
      {
        test: /\.css$/i,
        use: ["style-loader", "css-loader"],
      },
      {
        test: /\.?js$/,
        exclude: /node_modules/,
        use: {
          loader: "babel-loader",
          options: {
            presets: ['@babel/preset-env', '@babel/preset-react']
          }
        }
      }
    ]
  },
  plugins: []
}