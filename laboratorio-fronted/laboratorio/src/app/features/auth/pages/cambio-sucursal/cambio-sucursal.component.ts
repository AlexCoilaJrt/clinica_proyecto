import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { HeaderComponent } from '../../../../shared/header/header.component';
import { SidebarComponent } from '../../../../shared/sidebar/sidebar.component';

interface Sucursal {
  id: number;
  nombre: string;
}

@Component({
  selector: 'app-cambio-sucursal',
  standalone: true,
  imports: [CommonModule, FormsModule, HeaderComponent, SidebarComponent],
  templateUrl: './cambio-sucursal.component.html',
  styleUrls: ['./cambio-sucursal.component.css']
})
export class CambioSucursalComponent implements OnInit {
  usuario: string = '';
  nuevaContrasena: string = '';
  sucursalSeleccionada: string = '';
  
  sucursales: Sucursal[] = [
    { id: 2, nombre: 'Sucursal norte' },
    { id: 3, nombre: 'Sucursal sur' },
    { id: 4, nombre: 'Sucursal centro' },
    { id: 5, nombre: 'Sucursal este' }
  ];

  alertaMostrada: boolean = false;
  alertaTipo: 'success' | 'error' = 'success';
  alertaMensaje: string = '';

  constructor(private router: Router) {}

  ngOnInit() {
    // Obtener usuario del localStorage
    const userStorage = localStorage.getItem('currentUser');
    if (userStorage) {
      try {
        const user = JSON.parse(userStorage);
        this.usuario = user.username || '';
      } catch (e) {
        console.error('Error al cargar usuario', e);
      }
    }
  }

  mostrarAlerta(tipo: 'success' | 'error', mensaje: string) {
    this.alertaTipo = tipo;
    this.alertaMensaje = mensaje;
    this.alertaMostrada = true;

    setTimeout(() => {
      this.alertaMostrada = false;
    }, 4000);
  }

  guardarCambios() {
    // Validar que todos los campos estén llenos
    if (!this.usuario || !this.nuevaContrasena || !this.sucursalSeleccionada) {
      this.mostrarAlerta('error', 'Por favor complete todos los campos');
      return;
    }

    // Validar longitud mínima de contraseña
    if (this.nuevaContrasena.length < 6) {
      this.mostrarAlerta('error', 'La contraseña debe tener al menos 6 caracteres');
      return;
    }

    // Aquí iría la llamada al servicio para guardar los cambios
    // Simulación de guardado exitoso
    this.mostrarAlerta('success', 'Se guardó exitosamente...');
    
    // Guardar en localStorage
    localStorage.setItem('sucursal', this.sucursalSeleccionada);
    
    // Actualizar el usuario en localStorage si cambió
    const userStorage = localStorage.getItem('currentUser');
    if (userStorage) {
      try {
        const user = JSON.parse(userStorage);
        user.sucursal = this.sucursalSeleccionada;
        localStorage.setItem('currentUser', JSON.stringify(user));
      } catch (e) {
        console.error('Error al actualizar usuario', e);
      }
    }
    
    // Opcional: redirigir al dashboard después de 2 segundos
    setTimeout(() => {
      // this.router.navigate(['/dashboard']);
    }, 2000);
  }

  cerrarSesion() {
    // Este método se ejecuta cuando el header emite el evento onCerrarSesion
    console.log('Cerrando sesión desde cambio-sucursal component');
  }
}