import { Component, OnInit, OnDestroy } from '@angular/core';
import { Subscription } from 'rxjs';
import { CommonModule } from '@angular/common';
import { Router, RouterOutlet } from '@angular/router';

import { LoggedUser } from '@pichincha/ds-core';
import { JwtAuthService } from '../../services/jwt-auth.service';
import { HeaderComponent } from '../../components/header/header.component';
import { FooterComponent } from '../../components/footer/footer.component';
import { SidebarComponent } from '../../components/sidebar/sidebar.component';

@Component({
  selector: 'app-fullpage-with-header',
  standalone: true,
  imports: [
    CommonModule,
    RouterOutlet,
    HeaderComponent,
    FooterComponent,
    SidebarComponent
  ],
  templateUrl: "./fullpage-with-header.component.html",
  styleUrls: ["./fullpage-with-header.component.scss"]
})
export class FullpageWithHeaderComponent implements OnInit, OnDestroy {
  userInfo: LoggedUser | undefined = undefined;
  private subscription = new Subscription();

  constructor(private jwtAuthService: JwtAuthService, private router: Router) {}

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

  ngOnInit(): void {
    this.subscription.add(
      this.jwtAuthService.isAuthenticated$.subscribe(isAuth => {
        if (isAuth) {
          const jwtUserInfo = this.jwtAuthService.getUserInfo();
          this.userInfo = {
            name: jwtUserInfo?.username || 'Usuario',
          } as LoggedUser;
        } else {
          this.userInfo = undefined;
        }
      })
    );
  }

  ngOnDestroy(): void {
    this.subscription.unsubscribe();
  }

  onCloseSession(): void {
    console.log("Cerrando sesión full");
    this.jwtAuthService.logout();
  }
  
  handleClickMenuItem(route: any): void {
    const { detail } = route as CustomEvent<any>;
    this.router.navigate([detail.value]);
  }
}