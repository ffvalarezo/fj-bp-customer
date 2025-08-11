import { Component, inject, OnInit, ViewEncapsulation } from "@angular/core";
import { Router } from "@angular/router";

import { LoginService } from "./services/login.service";
// import { injectWebComponent } from "./helper/inject-web-component"; // Usar cuando se tenga un microfrontend de tipo Web Component.

@Component({
  selector: "app-root-base",
  templateUrl: "./app.component.html",
  encapsulation: ViewEncapsulation.ShadowDom,
})
export class AppComponent implements OnInit {
  title = "angular-container";
  total: number | undefined;

  router = inject(Router);

  loginService = inject(LoginService);

  ngOnInit(): void {
     /*  injectWebComponent(); // Usar cuando se tenga un microfrontend de tipo Web Component. */
    this.loginService.initialConfig();
  }
}
