import {Environment} from "../app/interfaces/environment.interface";

export const environment: Environment = {
  production: false,
  apiUrl: 'https://dummyjson.com',
  mainPath: '/main',
  timeSession: 300,
  authProvider: {
    accessTokenName: 'access_token',
    authToken: 'auth_token'
  },
  storage: {
    key: '<6aR!DZj5)',
  },
  translationFiles: 'http://localhost:4200/assets/locales/',
};
