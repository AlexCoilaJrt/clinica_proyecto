import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import Swal from 'sweetalert2';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent {
  usuario: string = '';
  contrasena: string = '';
  mostrarContrasena: boolean = false;
  error: boolean = false;
  mensajeError: string = '';
  cargando: boolean = false;

  constructor(private router: Router, private authService: AuthService) { }

  toggleMostrarContrasena() {
    this.mostrarContrasena = !this.mostrarContrasena;
  }

  async iniciarSesion() {
    // Resetear el estado de error antes de intentar login
    this.error = false;
    this.mensajeError = '';

    // Validación básica
    if (!this.usuario || !this.contrasena) {
      this.error = true;
      this.mensajeError = 'Por favor complete todos los campos';
      return;
    }



    this.authService.login(this.usuario, this.contrasena).subscribe({
      next: (response) => {
    this.cargando = true;
        const user = {
          username: this.usuario,
          rol: response.role,
          token: response.token
        };
        
        localStorage.setItem('currentUser', JSON.stringify(user));
        localStorage.setItem('token', response.token);

        // Redirigir
        this.router.navigate(['/cambio-sucursal']);
      },
      error: (err) => {
        this.error = true;
        
        let mensajeError = 'Ha ocurrido un error inesperado.';

        // Extraer el mensaje específico del backend
        if (err.error && err.error.message) {
          mensajeError = err.error.message;
        }

        this.mensajeError = mensajeError;

        // Mostrar SweetAlert
        Swal.fire({
          icon: 'error',
          title: 'Error de Autenticación',
          text: mensajeError,
          confirmButtonText: 'Entendido',
          confirmButtonColor: '#dc2626'
        }).then((result) => {
          console.log('Usuario ha reconocido el error de autenticación.');
          // Mantener el estado de error para que los campos se vean en rojo
          // No navegamos de vuelta a login porque ya estamos en login
        });
      },
      complete: () => {
        this.cargando = false;
      }
    });
  }

  onSubmit(event: Event) {
    event.preventDefault();
    this.iniciarSesion();
  }
}