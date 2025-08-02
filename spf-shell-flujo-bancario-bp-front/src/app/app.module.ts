import { NgModule } from "@angular/core";
import { BrowserModule } from "@angular/platform-browser";
import { HTTP_INTERCEPTORS, provideHttpClient, withInterceptorsFromDi } from "@angular/common/http";

import { EventBusModule } from "@pichincha/angular-sdk/eventbus";
import { HttpModule } from "@pichincha/angular-sdk/http";
import { StorageModule } from "@pichincha/angular-sdk/storage";
import { AuthModule, AuthService, EStorageType } from "@pichincha/angular-sdk/auth";

import { AppComponent } from "./app.component";
import { DashboardComponent } from "./layouts/dashboard/dashboard.component";
import { FullpageWithHeaderComponent } from "./layouts/fullpage-with-header/fullpage-with-header.component";

import { AppRoutingModule } from "./app-routing.module";
import { JwtInterceptor } from "./interceptors/jwt.interceptor";

import { environment } from "../environments/environment";
import { azureConfig } from "./config/auth.config";
import { LoginService } from "./services/login.service";

@NgModule({
  declarations: [AppComponent],
  imports: [
    BrowserModule,
    AppRoutingModule,
    AuthModule.forRoot(azureConfig as any),

    /* --- pichincha-angular-sdk ---*/
    EventBusModule,
    StorageModule.forRoot({
      storageType: EStorageType.SESSION,
      secretKey: environment.storage.key,
    }),
    HttpModule.forRoot({ api_url: environment.apiUrl }),

    /* ---- components ---- */
    DashboardComponent,
    FullpageWithHeaderComponent,
  ],
  providers: [
    provideHttpClient(withInterceptorsFromDi()),
    AuthService,
    LoginService,
    {
      provide: HTTP_INTERCEPTORS,
      useClass: JwtInterceptor,
      multi: true
    }
  ],
  bootstrap: [AppComponent],
  schemas: [],
})
export class AppModule {}
