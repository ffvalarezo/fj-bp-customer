const ModuleFederationPlugin = require("webpack/lib/container/ModuleFederationPlugin");
const mf = require("@angular-architects/module-federation/webpack");
const path = require("path");
const share = mf.share;

const sharedMappings = new mf.SharedMappings();
sharedMappings.register(path.join(__dirname, "tsconfig.json"), [
  "src/app/services/jwt-auth.service",
]);

module.exports = {
  output: {
    uniqueName: "angularContainer",
    publicPath: "auto",
    scriptType: "text/javascript",
  },
  optimization: {
    runtimeChunk: false,
  },
  resolve: {
    alias: {
      ...sharedMappings.getAliases(),
    },
  },
  experiments: {
    outputModule: true,
  },
  module: {
    rules: [
      {
        test: /\.m?js$/,
        resolve: {
          fullySpecified: false, // Asegura que los archivos ESM se resuelvan correctamente
        },
        type: "javascript/auto", // Indica a Webpack que maneje el archivo como un módulo ESM
      },
    ],
  },
  plugins: [
    new ModuleFederationPlugin({
      name: "shell",
      remotes: {
        mf_cliente_app:
          "mf_cliente_app@http://localhost:4201/spf-mfa-flujo-bancario-bp-cliente/remoteEntry.js",
        mf_movimiento_app:
          "mf_movimiento_app@http://localhost:4202/spf-mfa-flujo-bancario-bp-movimiento/remoteEntry.js",
        mf_transaccion_app:
          "mf_transaccion_app@http://localhost:4203/remoteEntry.js",
      },
      shared: share({
        "@angular/core": {
          singleton: true,
          strictVersion: true,
          requiredVersion: "auto",
        },
        "@angular/common": {
          singleton: true,
          strictVersion: true,
          requiredVersion: "auto",
        },
        "@angular/forms": {
          singleton: true,
          strictVersion: true,
          requiredVersion: "auto",
        },
        "@angular/common/http": {
          singleton: true,
          strictVersion: true,
          requiredVersion: "auto",
        },
        "@angular/router": {
          singleton: true,
          strictVersion: true,
          requiredVersion: "auto",
        },
        "@pichincha/ds-core": {
          singleton: true,
          strictVersion: true,
          requiredVersion: "auto",
        },
        "@pichincha/ds-angular": {
          singleton: true,
          strictVersion: true,
          requiredVersion: "auto",
        },
        ...sharedMappings.getDescriptors(),
      }),
    }),
    sharedMappings.getPlugin(),
  ],
};
