import { Component, Input, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Orden } from '../models/orden.model';

@Component({
  selector: 'app-procesar-orden-modal',
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
          <h3 class="text-lg font-semibold text-gray-900">Procesar Orden</h3>
          <button (click)="cerrar.emit()" class="text-gray-400 hover:text-gray-600">
            <svg class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12"/>
            </svg>
          </button>
        </div>

        <!-- Contenido -->
        <div class="mb-6">
          <p class="text-sm text-gray-700">
            Procesando orden para: <span class="font-semibold">{{ orden.patientFirstName }} {{ orden.patientLastName }}</span>
          </p>
        </div>

        <!-- Botones de acción -->
        <div class="flex space-x-3 mb-4">
          <button (click)="verResultados()" 
                  class="flex-1 px-4 py-2 bg-cyan-500 text-white rounded hover:bg-cyan-600">
            Ver Resultados
          </button>
          <button (click)="verExamenes()" 
                  class="flex-1 px-4 py-2 bg-cyan-500 text-white rounded hover:bg-cyan-600">
            Ver Exámenes
          </button>
        </div>

        <!-- Footer -->
        <div class="flex justify-end">
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
export class ProcesarOrdenModalComponent {
  @Input() orden: Orden | null = null;
  @Input() mostrar: boolean = false;
  @Output() cerrar = new EventEmitter<void>();
  @Output() abrirResultados = new EventEmitter<void>();
  @Output() abrirExamenes = new EventEmitter<void>();

  verResultados() {
    this.abrirResultados.emit();
    // Aquí puedes navegación a vista de resultados o abrir otro modal
  }

  verExamenes() {
    this.abrirExamenes.emit();
    // Aquí puedes navegar a vista de exámenes o abrir otro modal
  }
}
