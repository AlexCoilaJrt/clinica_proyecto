import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, NavigationEnd } from '@angular/router';
import { trigger, state, style, transition, animate } from '@angular/animations';
import { filter } from 'rxjs/operators';

@Component({
  selector: 'app-sidebar',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './sidebar.component.html',
  styleUrls: ['./sidebar.component.css'],
  animations: [
    trigger('slideDown', [
      transition(':enter', [
        style({ height: '0', opacity: 0, overflow: 'hidden' }),
        animate('200ms ease-out', style({ height: '*', opacity: 1 }))
      ]),
      transition(':leave', [
        style({ height: '*', opacity: 1, overflow: 'hidden' }),
        animate('200ms ease-in', style({ height: '0', opacity: 0 }))
      ])
    ])
  ]
})
export class SidebarComponent implements OnInit {
  // Estados de expansión
  configuracionesExpandido: boolean = false;
  procesosExpandido: boolean = false;
  reportesExpandido: boolean = false;

  // Ruta actual
  rutaActual: string = '';
  usuarioActual: string = 'Usuario';
  userGender: string = 'null';
    bien: string = 'null';
  constructor(private router: Router) {
    // Escuchar cambios de ruta
    this.router.events.pipe(
      filter(event => event instanceof NavigationEnd)
    ).subscribe((event: any) => {
      this.rutaActual = event.url;
      this.expandirMenuSegunRuta(event.url);
    });
  }

  ngOnInit() {
    this.cargarPerfil();
    this.rutaActual = this.router.url;
    this.expandirMenuSegunRuta(this.rutaActual);
  }
  cargarPerfil(): void {
    const userStorage = localStorage.getItem('currentUser');
    if (userStorage) {
      try {
        const user = JSON.parse(userStorage);
        this.usuarioActual = user.firstName + ' ' + (user.lastName || '');
        console.log('Usuario cargado en sidebar:', this.usuarioActual);
        this.userGender = user.sexo || 'null';
      } catch (e) {
        this.usuarioActual = 'Usuario';
      }
    }
  }
  getProfileImage(): string {
  if (this.userGender === 'M') {
    this.bien = 'Bienvenido';
    return '/male-avatar.jpg';
  } else if (this.userGender === 'F') {
    this.bien = 'Bienvenida';
    return '/female-avatar.png';
  } else {
    this.bien = 'Bienvenide';
    return '/default-avatar.png';
  }
}
  // Expandir automáticamente el menú según la ruta actual
  expandirMenuSegunRuta(url: string) {
    if (url.includes('/configuraciones')) {
      this.configuracionesExpandido = true;
    } else if (url.includes('/procesos')) {
      this.procesosExpandido = true;
    } else if (url.includes('/reportes')) {
      this.reportesExpandido = true;
    }
  }

  // Toggle para cada sección
  toggleConfiguraciones() {
    this.configuracionesExpandido = !this.configuracionesExpandido;
  }

  toggleProcesos() {
    this.procesosExpandido = !this.procesosExpandido;
  }

  toggleReportes() {
    this.reportesExpandido = !this.reportesExpandido;
  }

  // Navegación
  navegarA(ruta: string) {
    this.router.navigate([ruta]);
  }
}