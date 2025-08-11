import { Component, inject, OnInit } from "@angular/core";
import {
  PichinchaButtonModule,
  PichinchaTypographyModule,
} from "@pichincha/ds-angular";

import { LoginService } from "../../services/login.service";

@Component({
  selector: "app-simple-auth-redirect",
  standalone: true,
  imports: [PichinchaTypographyModule, PichinchaButtonModule],
  templateUrl: "./simple-auth-redirect.component.html",
  styleUrls: ["./simple-auth-redirect.component.scss"],
})
export class SimpleAuthRedirectComponent implements OnInit {
  loginService = inject(LoginService);

  constructor() {}

  get loading(): boolean {
    return this.loginService.getLoadingStorage();
  }

  async ngOnInit() {
    this.loginService.detectActivateAccount();
  }

  handlerLogin() {
    this.loginService.activateAccount();
  }
}
