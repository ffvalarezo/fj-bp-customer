import { authGuard } from "./auth.guard";
import { inject } from "@angular/core";


jest.mock("@angular/core", () => ({
  inject: jest.fn(),
}));

jest.mock("@angular/router", () => ({
  Router: jest.fn(),
}));

jest.mock("../services/login.service", () => ({
  LoginService: jest.fn(),
}));

describe("authGuard", () => {

  afterEach(() => {
    jest.restoreAllMocks();
  });

  it("should navigate to /auth if token response is falsy", async () => {
    const loginServiceMock = {
      getToken: () => null,
      isAuthenticated: () => false,
    } as any;

    const routerMock = { navigate: jest.fn() } as any;

    (inject as jest.Mock)
      .mockReturnValueOnce(loginServiceMock)
      .mockReturnValueOnce(routerMock);

    await authGuard();

    expect(routerMock.navigate).toHaveBeenCalledWith(["/auth"]);
  });

  it("should return true if token response is truthy", async () => {
    const loginServiceMock = {
      getToken: () => "token",
      isAuthenticated: () => false,
    } as any;

    const routerMock = { navigate: jest.fn() } as any;

    (inject as jest.Mock)
      .mockReturnValueOnce(loginServiceMock)
      .mockReturnValueOnce(routerMock);

    const result = await authGuard();

    expect(result).toBe(true);
  });

  it("should return true if user is already authenticated", async () => {
    const loginServiceMock = {
      getToken: () => null,
      isAuthenticated: () => true,
    } as any;

    const routerMock = { navigate: jest.fn() } as any;

    (inject as jest.Mock)
      .mockReturnValueOnce(loginServiceMock)
      .mockReturnValueOnce(routerMock);

    const result = await authGuard();

    expect(result).toBe(true);
  });

  it("should return false if an error occurs", async () => {
    (inject as jest.Mock).mockRejectedValueOnce("");

    const result = await authGuard();

    expect(result).toBe(false);
  });
});
