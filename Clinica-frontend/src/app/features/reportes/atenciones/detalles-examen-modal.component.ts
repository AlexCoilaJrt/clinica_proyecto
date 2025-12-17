import { Component, Input, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Orden } from '../models/orden.model';

@Component({
  selector: 'app-detalles-examen-modal',
  standalone: true,
  imports: [CommonModule],
  template: `
    <!-- Overlay del modal -->
    <div *ngIf="mostrar && orden" 
         class="fixed inset-0 bg-gray-900/30 backdrop-blur-sm flex items-center justify-center z-50 transition-all duration-300"
         (click)="cerrar.emit()">
      
      <!-- Contenido del modal -->
      <div class="bg-white rounded-lg shadow-xl w-full max-w-lg p-6 transform transition-all"
           (click)="$event.stopPropagation()">
        
        <!-- Header -->
        <div class="flex justify-between items-center mb-4">
          <h3 class="text-lg font-semibold text-gray-900">Detalles del Examen</h3>
          <button (click)="cerrar.emit()" class="text-gray-400 hover:text-gray-600">
            <svg class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12"/>
            </svg>
          </button>
        </div>

        <!-- Contenido -->
        <div class="space-y-3 text-sm">
          <div>
            <span class="font-semibold">Paciente:</span>
            <span class="ml-2">{{ orden.patientFirstName }} {{ orden.patientLastName }}</span>
          </div>
          
          <div>
            <span class="font-semibold">Documento:</span>
            <span class="ml-2">{{ orden.numeroOrden }}</span>
          </div>
          
          <div>
            <span class="font-semibold">Fecha:</span>
            <span class="ml-2">{{ formatearFecha(orden.fechaOrden) }}</span>
          </div>
          
          <div>
            <span class="font-semibold">Médico Sol:</span>
            <span class="ml-2">{{ orden.medicoNombre || '-' }}</span>
          </div>
          
          <div>
            <span class="font-semibold">Total:</span>
            <span class="ml-2">{{ orden.total }}</span>
          </div>
          
          <div>
            <span class="font-semibold">Estado Atención:</span>
            <span class="ml-2">{{ orden.estado }}</span>
          </div>
          
          <div>
            <span class="font-semibold">Estado Examen:</span>
            <span class="ml-2">Pendiente</span>
          </div>
          
          <p class="text-gray-600 italic mt-4">
            Aquí puedes poner más información detallada sobre los resultados o exámenes.
          </p>
        </div>

        <!-- Footer -->
        <div class="mt-6 flex justify-end">
          <button (click)="cerrar.emit()" 
                  class="px-4 py-2 bg-gray-500 text-white rounded hover:bg-gray-600">
            Cerrar
          </button>
        </div>
      </div>
    </div>
  `,
  styles: [`
    :host {
      display: contents;
    }
  `]
})
export class DetallesExamenModalComponent {
  @Input() orden: Orden | null = null;
  @Input() mostrar: boolean = false;
  @Output() cerrar = new EventEmitter<void>();

  formatearFecha(fecha: string): string {
    if (!fecha) return '';
    const date = new Date(fecha);
    return date.toLocaleDateString('es-PE', {
      day: '2-digit',
      month: '2-digit',
      year: 'numeric',
      hour: '2-digit',
      minute: '2-digit'
    });
  }
}
