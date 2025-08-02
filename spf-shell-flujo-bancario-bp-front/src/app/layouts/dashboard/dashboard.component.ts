import { Component, OnInit, OnDestroy } from '@angular/core';
import { Router, RouterOutlet } from '@angular/router';
import { Subscription } from 'rxjs';
import { CommonModule } from '@angular/common';

// Design System Pichincha
import {
  PichinchaButtonModule,
  PichinchaIconModule,
  PichinchaGridModule,
  PichinchaTypographyModule
} from '@pichincha/ds-angular';

import { JwtAuthService } from '../../services/jwt-auth.service';

// Components
import { HeaderComponent } from '../../components/header/header.component';
import { FooterComponent } from '../../components/footer/footer.component';
import { SidebarComponent } from '../../components/sidebar/sidebar.component';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [
    CommonModule,
    RouterOutlet,
    PichinchaButtonModule,
    PichinchaIconModule,
    PichinchaGridModule,
    PichinchaTypographyModule,
    HeaderComponent,
    FooterComponent,
    SidebarComponent
  ],
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.scss']
})
export class DashboardComponent implements OnInit, OnDestroy {
  userInfo: any = null;
  isAuthenticated = false;
  private subscription = new Subscription();

  // Menu items para el sidebar
  menuItems: any[] = [
    {
      label: 'Inicio',
      value: '/home',
      iconName: 'home'
    },
    {
      label: 'Clientes',
      value: '/cliente',
      iconName: 'person'
    },
    {
      label: 'Cuentas',
      value: '/movimiento',
      iconName: 'swap_horiz'
    },
    {
      label: 'Transacciones',
      value: '/transaccion',
      iconName: 'payment'
    }
  ];

  // Configuración de microfrontends usando Design System
  microfrontends = [
    {
      id: 'cliente',
      title: 'Gestión de Clientes',
      description: 'Administra la información de clientes, consulta datos y gestiona perfiles.',
      route: 'cliente',
      icon: 'person',
      color: 'primary',
      actions: ['Nuevo Cliente', 'Buscar Cliente', 'Reportes']
    },
    {
      id: 'movimiento',
      title: 'Consulta de Movimientos',
      description: 'Revisa el historial de transacciones y movimientos bancarios.',
      route: 'movimiento',
      icon: 'swap_horiz',
      color: 'secondary',
      actions: ['Ver Movimientos', 'Filtrar', 'Exportar']
    },
    {
      id: 'transaccion',
      title: 'Gestión de Transacciones',
      description: 'Procesa y registra nuevas transacciones bancarias.',
      route: 'transaccion',
      icon: 'payment',
      color: 'accent',
      actions: ['Nueva Transacción', 'Autorizar', 'Revisar']
    }
  ];

  constructor(
    private jwtAuthService: JwtAuthService,
    private router: Router
  ) { }

  ngOnInit(): void {
    this.subscription.add(
      this.jwtAuthService.isAuthenticated$.subscribe(isAuth => {
        this.isAuthenticated = isAuth;
        if (isAuth) {
          this.userInfo = this.jwtAuthService.getUserInfo();
        }
      })
    );
  }

  ngOnDestroy(): void {
    this.subscription.unsubscribe();
  }

  navigateTo(route: string): void {
    this.router.navigate([route]);
  }

  onCardAction(microfrontend: any, action: string): void {
    console.log(`Acción ${action} en ${microfrontend.title}`);
    this.navigateTo(microfrontend.route);
  }

  handleClickMenuItem(route: any): void {
    const { detail } = route as CustomEvent<any>;
    this.router.navigate([detail.value]);
  }
}