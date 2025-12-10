import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';

@Component({
  selector: 'app-portal-clinico-login',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './portal-clinico-login.component.html',
  styleUrls: ['./portal-clinico-login.component.css']
})
export class PortalClinicoLoginComponent {
  admin: string = '';
  contrasena: string = '';
  mostrarContrasena: boolean = false;
  error: boolean = false;
  mensajeError: string = '';
  cargando: boolean = false;

  constructor(private router: Router) {}

  toggleMostrarContrasena() {
    this.mostrarContrasena = !this.mostrarContrasena;
  }

  async iniciarSesion() {
    this.error = false;
    this.mensajeError = '';

    // Validación básica
    if (!this.admin || !this.contrasena) {
      this.error = true;
      this.mensajeError = 'Usuario invalido';
      return;
    }

    this.cargando = true;

    // Simulación de llamada a API
    try {
      // Aquí iría tu servicio de autenticación
      await new Promise(resolve => setTimeout(resolve, 1500));

      // Simulación de validación exitosa
      if (this.admin === 'admin' && this.contrasena === 'admin') {
        localStorage.setItem('usuario', this.admin);
        localStorage.setItem('token', 'token_simulado_123');
        
        this.router.navigate(['/cambio-sucursal']);
      } else {
        this.error = true;
        this.mensajeError = 'Contraseña incorrecta';
      }
    } catch (error) {
      this.error = true;
      this.mensajeError = 'Error al iniciar sesión';
    } finally {
      this.cargando = false;
    }
  }

  onSubmit(event: Event) {
    event.preventDefault();
    this.iniciarSesion();
  }
}
