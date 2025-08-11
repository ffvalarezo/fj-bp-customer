import { authGuard } from "../guards/auth.guard";
import { DashboardComponent } from "../layouts/dashboard/dashboard.component";
import { ListLayout, Guards } from "./layouts";

describe("layouts", () => {
  describe("ListLayout", () => {
    it("should contain DashboardComponent and FullpageWithHeaderComponent", () => {
      expect(ListLayout.dashboard).toEqual(DashboardComponent);
    });
  });

  describe("Guards", () => {
    it("should contain LoggedGuard", () => {
      expect(Guards.logger).toEqual(authGuard);
    });
  });
});
