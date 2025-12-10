import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, NavigationEnd, RouterLink, RouterModule } from '@angular/router';
import { filter } from 'rxjs/operators';

@Component({
  selector: 'app-sidebar',
  standalone: true,
  imports: [CommonModule, RouterLink, RouterModule],
  templateUrl: './sidebar.component.html',
  styleUrls: ['./sidebar.component.css']
})
export class SidebarComponent implements OnInit {
  configuracionExpandida: boolean = true;
  usuariosExpandido: boolean = true;
  rutaActual: string = '';

constructor(private router: Router) {
    this.router.events
      .pipe(filter(event => event instanceof NavigationEnd))
      .subscribe((event: NavigationEnd) => {
        this.rutaActual = event.urlAfterRedirects;
        this.configuracionExpandida = this.esRutaConfiguracion(this.rutaActual);
        this.usuariosExpandido = this.esRutaUsuarios(this.rutaActual);
      });
  }

ngOnInit() {
    this.rutaActual = this.router.url;
    this.configuracionExpandida = this.esRutaConfiguracion(this.rutaActual);
    this.usuariosExpandido = this.esRutaUsuarios(this.rutaActual);
  }

  toggleConfiguracion() {
    this.configuracionExpandida = !this.configuracionExpandida;
  }
  toggleUsuarios() {
    this.usuariosExpandido = !this.usuariosExpandido;
  }

  navegarA(ruta: string) {
    this.router.navigate([ruta]);
  }

  esRutaConfiguracion(ruta: string): boolean {
    const rutasConfiguracion = ['/cambio-sucursal', '/cie10', '/datos-medicos', '/horario'];
    return rutasConfiguracion.some(r => ruta.includes(r));
  }
  esRutaUsuarios(url: string): boolean {
    // Ejemplo de rutas para Usuarios
    return url.includes('/usuarios/gestion') ||
           url.includes('/usuarios/roles') ||
           url.includes('/usuarios/permisos');
  }
}
