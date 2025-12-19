import { Component, Input, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-pagination',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <div class="flex flex-col sm:flex-row justify-between items-center mt-6 gap-4">
      <!-- Selector de cantidad -->
      <div class="flex items-center gap-3 bg-gray-50 px-4 py-2 rounded-lg border border-gray-200">
        <label class="text-sm font-medium text-gray-700">Mostrar</label>
        <select 
          [ngModel]="pageSize" 
          (ngModelChange)="onPageSizeChange($event)"
          class="border-2 border-gray-300 rounded-lg px-3 py-1.5 text-sm font-medium focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-blue-500 bg-white">
          <option *ngFor="let size of pageSizeOptions" [value]="size">{{ size }}</option>
        </select>
        <span class="text-sm font-medium text-gray-700">{{ recordsLabel }}</span>
      </div>

      <!-- Controles de paginación -->
      <nav class="flex items-center gap-2">
        <!-- Botón Anterior -->
        <button 
          (click)="onPageChange(currentPage - 1)" 
          [disabled]="currentPage === 0"
          [class.opacity-50]="currentPage === 0" 
          [class.cursor-not-allowed]="currentPage === 0"
          class="px-4 py-2 text-sm font-medium border-2 border-gray-300 rounded-lg hover:bg-gray-100 transition-all disabled:hover:bg-white disabled:cursor-not-allowed flex items-center gap-2">
          <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 19l-7-7 7-7" />
          </svg>
          {{ previousLabel }}
        </button>

        <!-- Números de página -->
        <div class="hidden sm:flex items-center gap-1">
          <button
            *ngFor="let page of getVisiblePages()"
            (click)="onPageChange(page)" 
            [class.bg-gradient-to-r]="page === currentPage"
            [class.from-blue-600]="page === currentPage"
            [class.to-indigo-600]="page === currentPage" 
            [class.text-white]="page === currentPage"
            [class.border-blue-600]="page === currentPage" 
            [class.shadow-md]="page === currentPage"
            [class.hover:bg-gray-100]="page !== currentPage"
            class="min-w-[40px] px-3 py-2 text-sm font-semibold border-2 border-gray-300 rounded-lg transition-all">
            {{ page + 1 }}
          </button>
        </div>

        <!-- Botón Siguiente -->
        <button 
          (click)="onPageChange(currentPage + 1)" 
          [disabled]="currentPage >= totalPages - 1"
          [class.opacity-50]="currentPage >= totalPages - 1"
          [class.cursor-not-allowed]="currentPage >= totalPages - 1"
          class="px-4 py-2 text-sm font-medium border-2 border-gray-300 rounded-lg hover:bg-gray-100 transition-all disabled:hover:bg-white disabled:cursor-not-allowed flex items-center gap-2">
          {{ nextLabel }}
          <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5l7 7-7 7" />
          </svg>
        </button>
      </nav>
    </div>
  `
})
export class PaginationComponent {
  @Input() currentPage: number = 0;
  @Input() totalPages: number = 0;
  @Input() pageSize: number = 10;
  @Input() pageSizeOptions: number[] = [10, 25, 50, 100];
  @Input() maxVisiblePages: number = 5;
  
  // Textos personalizables
  @Input() recordsLabel: string = 'registros';
  @Input() previousLabel: string = 'Anterior';
  @Input() nextLabel: string = 'Siguiente';
  
  @Output() pageChange = new EventEmitter<number>();
  @Output() pageSizeChange = new EventEmitter<number>();

  onPageChange(page: number): void {
    if (page >= 0 && page < this.totalPages && page !== this.currentPage) {
      this.pageChange.emit(page);
    }
  }

  onPageSizeChange(newSize: number): void {
    this.pageSizeChange.emit(newSize);
  }

  getVisiblePages(): number[] {
    const halfVisible = Math.floor(this.maxVisiblePages / 2);
    let startPage = Math.max(0, this.currentPage - halfVisible);
    let endPage = Math.min(this.totalPages - 1, startPage + this.maxVisiblePages - 1);
    
    // Ajustar si estamos cerca del final
    if (endPage - startPage < this.maxVisiblePages - 1) {
      startPage = Math.max(0, endPage - this.maxVisiblePages + 1);
    }
    
    const pages: number[] = [];
    for (let i = startPage; i <= endPage; i++) {
      pages.push(i);
    }
    
    return pages;
  }
}