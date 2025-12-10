import { Component } from '@angular/core';
import { HeaderComponent } from "../../../shared/header/header.component";
import { SidebarComponent } from "../../../shared/sidebar/sidebar.component";

@Component({
  selector: 'app-gestion',
  imports: [HeaderComponent, SidebarComponent],
  templateUrl: './gestion.component.html',
  styleUrl: './gestion.component.css',
})
export class GestionComponent {
  cerrarSesion() {
    // Este método se ejecuta cuando el header emite el evento onCerrarSesion
    console.log('Cerrando sesión desde cambio-sucursal component');
  }
}