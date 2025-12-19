import { CommonModule } from '@angular/common';
import { Component, Input, Output, EventEmitter } from '@angular/core';

export interface Columna {
  field: string;
  header: string;
  tipo?: 'date' | 'text' | 'badge' | 'index' | 'area-badge' | 'nombre-exam' | 'area-info' | 'currency' | 'tiempo-entrega' | 'status';
  subField: string[];
}

@Component({
  selector: 'app-tabla-general',
  imports: [CommonModule],
  templateUrl: './tabla-general.component.html'
})
export class TablaGeneralComponent {
  @Input() datos: any[] = [];
  @Input() columnas: Columna[] = [];
  @Input() tituloAcciones: string = 'Acciones';
  @Input() currentPage: number = 0;
  @Input() pageSize: number = 10;
  // Eventos para avisar al padre
  @Output() onEdit = new EventEmitter<any>();
  @Output() onDelete = new EventEmitter<any>();
  @Output() onView = new EventEmitter<any>();
  @Output() onLinkClick = new EventEmitter<any>();
  @Output() viewSubExams = new EventEmitter<any>();
  @Output() viewDetails = new EventEmitter<any>();


}