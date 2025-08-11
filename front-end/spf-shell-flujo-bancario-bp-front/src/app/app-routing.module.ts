import { NgModule } from "@angular/core";
import { Route, RouterModule, Routes } from "@angular/router";

import { IRouteBase, RouterRender } from "@pichincha/angular-sdk/router-helper";
import { storage } from "@pichincha/angular-sdk/storage";
import { EStorageType } from "@pichincha/angular-sdk/auth";

import routesImport from "../assets/microapp.json";

import { Guards, ListLayout } from "./config/layouts";

import { environment } from "src/environments/environment";
import { SimpleAuthRedirectComponent } from "./pages/simple-auth-redirect/simple-auth-redirect.component";
import { TestJwtLoginComponent } from "./pages/test-jwt-login/test-jwt-login.component";
import { HomeComponent } from "./pages/home/home.component";
import { authGuard } from "./guards/auth.guard";
const routes = routesImport as unknown as IRouteBase[];
const storageConfig = storage({
  storageType: EStorageType.SESSION,
  secretKey: environment.storage.key,
});

const routerDetail = new RouterRender(routes, storageConfig);
const routesList: Routes = routerDetail.routerList(ListLayout, Guards);
const protectedRoutes = routesList.map((route) => ({
  ...route,
  canActivate: route.canActivate
    ? [...route.canActivate, authGuard]
    : [authGuard],
}));

const routesWithAuth = [
  {
    path: "auth",
    children: [
      {
        path: "",
        redirectTo: "test-jwt",
        pathMatch: "full",
      },
      {
        path: "login",
        component: SimpleAuthRedirectComponent,
      },
      {
        path: "test-jwt",
        component: TestJwtLoginComponent,
      },
    ],
  } as Route,
  {
    path: "home",
    component: ListLayout.dashboard,
    canActivate: [authGuard],
    children: [
      {
        path: "",
        component: HomeComponent,
      },
    ],
  } as Route,
  ...protectedRoutes,
  {
    path: "",
    redirectTo: "home",
    pathMatch: "full",
  } as Route,
  {
    path: "**",
    redirectTo: "home",
  } as Route,
];

@NgModule({
  imports: [RouterModule.forRoot(routesWithAuth)],
  exports: [RouterModule],
})
export class AppRoutingModule {}
