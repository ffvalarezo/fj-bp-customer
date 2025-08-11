import { Component, OnInit, inject } from "@angular/core";
import { CommonModule } from "@angular/common";
import { Router } from "@angular/router";

// Design System
import {
  PichinchaButtonModule,
  PichinchaIconModule,
} from "@pichincha/ds-angular";

// Services
import {
  MicrofrontendService,
  MicrofrontendInfo,
} from "../../services/microfrontend.service";
import { JwtAuthService } from "../../services/jwt-auth.service";

@Component({
  selector: "app-dashboard",
  standalone: true,
  imports: [CommonModule, PichinchaButtonModule, PichinchaIconModule],
  templateUrl: "./dashboard.component.html",
  styleUrls: ["./dashboard.component.scss"],
})
export class DashboardComponent implements OnInit {
  private microfrontendService = inject(MicrofrontendService);
  private jwtAuthService = inject(JwtAuthService);
  private router = inject(Router);

  microfrontends: MicrofrontendInfo[] = [];
  currentUser: any = null;

  ngOnInit(): void {
    this.loadUserInfo();
    this.loadMicrofrontends();
  }

  private loadUserInfo(): void {
    this.currentUser = this.jwtAuthService.getUserInfo();
  }

  private loadMicrofrontends(): void {
    this.microfrontends = this.microfrontendService.getMicrofrontendsInfo();

    // Verificar disponibilidad de microfrontends
    this.microfrontendService
      .checkMicrofrontendsAvailability()
      .subscribe((availableMicrofrontends) => {
        this.microfrontends = availableMicrofrontends;
      });
  }

  navigateToMicrofrontend(microfrontend: MicrofrontendInfo): void {
    const route = `/${microfrontend.route}`;
    this.router.navigate([route]);
  }

  getMicrofrontendStatus(mf: MicrofrontendInfo): string {
    return mf.isActive ? "Disponible" : "No disponible";
  }

  getMicrofrontendStatusClass(mf: MicrofrontendInfo): string {
    return mf.isActive ? "status-available" : "status-unavailable";
  }
}
