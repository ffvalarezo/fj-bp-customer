import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';

// Design System
import {
  PichinchaButtonModule,
  PichinchaIconModule
} from '@pichincha/ds-angular';

// Services
import { MicrofrontendService, MicrofrontendInfo } from '../../services/microfrontend.service';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [
    CommonModule,
    PichinchaButtonModule,
    PichinchaIconModule
  ],
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.scss']
})
export class HomeComponent implements OnInit {
  private microfrontendService = inject(MicrofrontendService);
  private router = inject(Router);

  microfrontends: MicrofrontendInfo[] = [];

  ngOnInit(): void {
    this.loadMicrofrontends();
  }

  private loadMicrofrontends(): void {
    this.microfrontends = this.microfrontendService.getMicrofrontendsInfo();
    
    // Verificar disponibilidad de microfrontends
    this.microfrontendService.checkMicrofrontendsAvailability().subscribe(
      (availableMicrofrontends) => {
        this.microfrontends = availableMicrofrontends;
      }
    );
  }

  navigateToMicrofrontend(microfrontend: MicrofrontendInfo): void {
    const route = `/${microfrontend.route}`;
    this.router.navigate([route]);
  }

  getMicrofrontendStatus(mf: MicrofrontendInfo): string {
    return mf.isActive ? 'Disponible' : 'No disponible';
  }

  getMicrofrontendStatusClass(mf: MicrofrontendInfo): string {
    return mf.isActive ? 'status-available' : 'status-unavailable';
  }
}
