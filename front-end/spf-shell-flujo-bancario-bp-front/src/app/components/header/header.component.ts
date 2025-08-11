import {
  Component,
  input,
  output,
  OnInit,
  OnDestroy,
  inject,
} from "@angular/core";
import { Router } from "@angular/router";
import { Subscription } from "rxjs";
import { CommonModule } from "@angular/common";

// Importaciones del Design System de Pichincha (solo las que necesitamos)
import {
  PichinchaButtonModule,
  PichinchaIconModule,
} from "@pichincha/ds-angular";
import { LoggedUser } from "@pichincha/ds-core";

// Servicios del proyecto
import { JwtAuthService } from "../../services/jwt-auth.service";

@Component({
  selector: "app-header",
  standalone: true,
  imports: [CommonModule, PichinchaButtonModule, PichinchaIconModule],
  templateUrl: "./header.component.html",
  styleUrls: ["./header.component.scss"],
})
export class HeaderComponent implements OnInit, OnDestroy {
  // Inputs para compatibilidad
  loggedUser = input<LoggedUser>();
  onClickCloseSession = output();

  // Estado interno
  private jwtAuthService = inject(JwtAuthService);
  private router = inject(Router);
  private subscription = new Subscription();

  currentUser: LoggedUser | null = null;
  isAuthenticated = false;

  // Opciones de navegación para pichincha-menu-bar
  navigationItems = [
    {
      label: "Inicio",
      url: "/home",
      active: false,
      target: "_self",
    },
    {
      label: "Clientes",
      url: "/cliente",
      active: false,
      target: "_self",
    },
  ];

  // Mapeo de rutas para navegación
  private routeMap: { [key: string]: string } = {
    Inicio: "/home",
    Clientes: "/cliente",
  };

  ngOnInit(): void {
    // Suscribirse al estado de autenticación
    this.subscription.add(
      this.jwtAuthService.isAuthenticated$.subscribe((isAuth) => {
        this.isAuthenticated = isAuth;
        if (isAuth) {
          this.updateUserInfo();
        } else {
          this.currentUser = null;
        }
        this.updateActiveNavigation();
      })
    );
  }

  ngOnDestroy(): void {
    this.subscription.unsubscribe();
  }

  private updateUserInfo(): void {
    const jwtUserInfo = this.jwtAuthService.getUserInfo();
    const inputUser = this.loggedUser();

    // Combinar información del JWT y del input
    this.currentUser = {
      name: jwtUserInfo?.username || inputUser?.name || "Usuario",
      ...inputUser,
    } as LoggedUser;
  }

  private updateActiveNavigation(): void {
    const currentUrl = this.router.url;
    this.navigationItems.forEach((item) => {
      const itemRoute = this.routeMap[item.label];
      item.active = itemRoute ? currentUrl.includes(itemRoute) : false;
    });
  }

  goToMicrofrontend(type: string = "cliente"): void {
    this.router.navigate([`/${type}`]);
  }

  // Cerrar sesión
  handleCloseSession(): void {
    this.jwtAuthService.logout();
    this.onClickCloseSession.emit();
  }

  get displayUser(): LoggedUser | null {
    return this.currentUser || this.loggedUser() || null;
  }

  navigateToSpecificItem(item: any, index: number): void {
    const route = this.routeMap[item.label];
    if (route) {
      this.navigationItems.forEach((navItem, i) => {
        navItem.active = i === index;
      });
      this.router.navigate([route]);
      this.onItemSelected.emit({
        item: item,
        label: item.label,
        route: route,
        timestamp: new Date().toISOString(),
      });
    }
  }
  onItemSelected = output<{
    item: any;
    label: string;
    route: string;
    timestamp: string;
  }>();

  navigateToItem(item: any, index?: number): void {
    const route = this.routeMap[item.label];
    if (route) {
      this.navigationItems.forEach((navItem, i) => {
        navItem.active = navItem.label === item.label;
      });
      this.router.navigate([route]);
      this.onItemSelected.emit({
        item: item,
        label: item.label,
        route: route,
        timestamp: new Date().toISOString(),
      });
    } else {
      console.warn("No se encontró ruta para:", item.label);
    }
  }
}
