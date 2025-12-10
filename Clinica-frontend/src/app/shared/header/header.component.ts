import { Component, Input, OnInit, OnDestroy, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';

@Component({
  selector: 'app-header',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.css']
})
export class HeaderComponent implements OnInit, OnDestroy {
  @Input() breadcrumb: string = 'Inicio';
  @Output() onCerrarSesion = new EventEmitter<void>();

  horaActual: string = '';
  usuarioActual: string = 'Usuario';
  menuUsuarioAbierto: boolean = false;
  private intervaloHora: any;

  constructor(private router: Router) {}

  ngOnInit() {
    this.actualizarHora();
    this.intervaloHora = setInterval(() => this.actualizarHora(), 1000);
    
    // Obtener usuario del localStorage
    const userStorage = localStorage.getItem('currentUser');
    if (userStorage) {
      try {
        const user = JSON.parse(userStorage);
        this.usuarioActual = user.username || 'Usuario';
      } catch (e) {
        this.usuarioActual = 'Usuario';
      }
    }

    // Cerrar menú al hacer clic fuera
    document.addEventListener('click', this.cerrarMenuUsuarioFuera.bind(this));
  }

  ngOnDestroy() {
    if (this.intervaloHora) {
      clearInterval(this.intervaloHora);
    }
    document.removeEventListener('click', this.cerrarMenuUsuarioFuera.bind(this));
  }

  actualizarHora() {
    const ahora = new Date();
    this.horaActual = ahora.toLocaleTimeString('es-ES', { 
      hour: '2-digit', 
      minute: '2-digit' 
    });
  }

  toggleMenuUsuario() {
    this.menuUsuarioAbierto = !this.menuUsuarioAbierto;
  }

  cerrarMenuUsuarioFuera(event: MouseEvent) {
    const target = event.target as HTMLElement;
    if (!target.closest('.w-8.h-8') && !target.closest('.absolute')) {
      this.menuUsuarioAbierto = false;
    }
  }

  verPerfil() {
    this.menuUsuarioAbierto = false;
    this.router.navigate(['/perfil']);
  }

  cambiarContrasena() {
    this.menuUsuarioAbierto = false;
    this.router.navigate(['/cambiar-contrasena']);
  }

  cerrarSesion() {
    this.menuUsuarioAbierto = false;
    localStorage.clear();
    this.onCerrarSesion.emit();
    this.router.navigate(['/login']);
  }
}
