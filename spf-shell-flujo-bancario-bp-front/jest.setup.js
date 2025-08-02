const { TextDecoder, TextEncoder } = require("node:util");

Object.defineProperties(globalThis, {
  TextDecoder: { value: TextDecoder, writable: true },
  TextEncoder: { value: TextEncoder, writable: true },
});

// Mock HttpService globally
jest.mock('@pichincha/angular-sdk/http', () => ({
  HttpService: jest.fn().mockImplementation(() => ({
    post: jest.fn(),
    get: jest.fn(),
    put: jest.fn(),
    delete: jest.fn(),
  }))
}));

// Mock StorageService globally
jest.mock('@pichincha/angular-sdk/storage', () => ({
  StorageService: jest.fn().mockImplementation(() => ({
    set: jest.fn(),
    get: jest.fn(),
    remove: jest.fn(),
  }))
}));

// Mock AuthService globally
jest.mock('@pichincha/angular-sdk/auth', () => ({
  AuthService: jest.fn().mockImplementation(() => ({
    initialize: jest.fn(),
    getAccountState: jest.fn(),
    getInteractionState: jest.fn(),
    isAuthenticated: jest.fn(),
    getAllAccounts: jest.fn(),
    authenticate: jest.fn(),
    handleAuthRedirect: jest.fn(),
  }))
}));
