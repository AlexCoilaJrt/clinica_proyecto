import { Component, Input, OnInit, OnDestroy, Output, EventEmitter, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { interval, Subscription } from 'rxjs';

import { HttpClientModule } from '@angular/common/http';
import { AuthService, TokenInfo } from '../../features/auth/services/auth.service';

@Component({
  selector: 'app-header',
  standalone: true,
  imports: [CommonModule, HttpClientModule],
  providers: [AuthService],
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.css']
})
export class HeaderComponent implements OnInit, OnDestroy {
  @Input() breadcrumb: string = 'Inicio';
  @Output() onCerrarSesion = new EventEmitter<void>();

  // Variables de estado
  tiempoRestanteMs: number = 0;
  tiempoFormateado: string = '30:00';

  // Timer
  private timerSubscription: Subscription | undefined;
  private syncSubscription: Subscription | undefined;

  usuarioActual: string = 'Usuario';
  userGender: string = 'null';
  bien: string = 'null';
  menuUsuarioAbierto: boolean = false;

  // Umbral de advertencia (5 minutos)
  private readonly UMBRAL_ADVERTENCIA = 300000; // 5 minutos en ms
  private advertenciaMostrada = false;

  constructor(
    private router: Router,
    private authService: AuthService,
    private cdr: ChangeDetectorRef
  ) { }

  ngOnInit() {
    this.iniciarTemporizador();
    this.cargarPerfil();

    // Cerrar menú al hacer clic fuera
    document.addEventListener('click', this.cerrarMenuUsuarioFuera.bind(this));
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
  iniciarTemporizador(): void {
    // Obtener tiempo inicial del servidor inmediatamente
    this.sincronizarConServidor();

    // Actualizar cada segundo localmente
    this.timerSubscription = interval(1000).subscribe(() => {
      // Solo decrementar si tenemos un tiempo válido
      if (this.tiempoRestanteMs > 0) {
        this.tiempoRestanteMs -= 1000;

        // Forzar actualización de la vista
        this.tiempoFormateado = this.formatearTiempo(this.tiempoRestanteMs);
        this.cdr.detectChanges();

        if (this.tiempoRestanteMs <= 0) {
          this.manejarSesionExpirada();
        } else {
          // Mostrar advertencia si quedan menos de 5 minutos
          if (this.tiempoRestanteMs <= this.UMBRAL_ADVERTENCIA && !this.advertenciaMostrada) {
            this.mostrarAdvertencia();
            this.advertenciaMostrada = true;
          }
        }
      }
    });

    // Sincronizar con el servidor cada 30 segundos
    this.syncSubscription = interval(30000).subscribe(() => {
      this.sincronizarConServidor();
    });
  }

  sincronizarConServidor(): void {
    this.authService.getTokenInfo().subscribe({
      next: (response) => {
        if (response.success && response.data) {
          const tokenInfo: TokenInfo = response.data;

          if (tokenInfo.isExpired) {
            this.manejarSesionExpirada();
          } else {
            this.tiempoRestanteMs = tokenInfo.timeRemainingMs;
            this.tiempoFormateado = this.formatearTiempo(this.tiempoRestanteMs);
            this.cdr.detectChanges(); // Forzar actualización tras sincronizar
          }
        }
      },
      error: (error) => {
        console.error('Error al sincronizar tiempo del token:', error);
        // Si falla la sincronización por 401, logout
        if (error.status === 401) {
          this.manejarSesionExpirada();
        }
      }
    });
  }

  mostrarAdvertencia(): void {
    const minutos = Math.floor(this.tiempoRestanteMs / 60000);
    alert(`⚠️ Su sesión expirará en ${minutos} minutos.Por favor, guarde su trabajo.`);
  }

  manejarSesionExpirada(): void {
    this.detenerTemporizador();
    this.tiempoFormateado = '00:00';

    alert('Su sesión ha expirado. Por favor, inicie sesión nuevamente.');
    this.cerrarSesion();
  }

  detenerTemporizador(): void {
    if (this.timerSubscription) {
      this.timerSubscription.unsubscribe();
    }
    if (this.syncSubscription) {
      this.syncSubscription.unsubscribe();
    }
  }

  formatearTiempo(ms: number): string {
    const totalSegundos = Math.floor(ms / 1000);
    const minutos = Math.floor(totalSegundos / 60);
    const segundos = totalSegundos % 60;

    const minutosStr = String(minutos).padStart(2, '0');
    const segundosStr = String(segundos).padStart(2, '0');

    return `${minutosStr}:${segundosStr} `;
  }

  ngOnDestroy(): void {
    this.detenerTemporizador();
    document.removeEventListener('click', this.cerrarMenuUsuarioFuera.bind(this));
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
    this.authService.logout();
    this.onCerrarSesion.emit();
  }
}