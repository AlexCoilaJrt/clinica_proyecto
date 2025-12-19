import { Component, Input, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';

type ModalSize = 'sm' | 'md' | 'lg' | 'xl' | '2xl';

@Component({
  selector: 'app-modal',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div *ngIf="isOpen"
      class="fixed inset-0 bg-black/60 backdrop-blur-sm flex items-center justify-center z-50 p-4 animate-fadeIn"
      (click)="onBackdropClick($event)">
      
      <div [class]="getModalClasses()" 
           (click)="$event.stopPropagation()">
        
        <!-- Header Modal con gradiente -->
        <div [class]="'bg-gradient-to-r ' + gradientFrom + ' ' + gradientTo + ' px-8 py-6'">
          <div class="flex justify-between items-center">
            <div class="flex items-center gap-3">
              <div *ngIf="showIcon" class="bg-white/20 backdrop-blur-sm p-2.5 rounded-xl">
                <svg class="w-6 h-6 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" [attr.d]="getIconPath()" />
                </svg>
              </div>
              <h3 class="text-2xl font-bold text-white">{{ title }}</h3>
            </div>
            <button 
              *ngIf="showCloseButton"
              (click)="close()"
              class="text-white/80 hover:text-white hover:bg-white/20 p-2 rounded-lg transition-all duration-200">
              <svg class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" />
              </svg>
            </button>
          </div>
        </div>

        <!-- Contenido del modal (ng-content) -->
        <div class="p-8">
          <ng-content></ng-content>
        </div>

        <!-- Footer (opcional) -->
        <div *ngIf="showFooter" class="flex justify-end gap-3 px-8 pb-8 pt-0 border-t border-gray-200 mt-6">
          <ng-content select="[footer]"></ng-content>
        </div>
      </div>
    </div>
  `,
  styles: [`
    @keyframes fadeIn {
      from { opacity: 0; }
      to { opacity: 1; }
    }

    @keyframes slideUp {
      from {
        transform: translateY(20px);
        opacity: 0;
      }
      to {
        transform: translateY(0);
        opacity: 1;
      }
    }

    .animate-fadeIn {
      animation: fadeIn 0.3s ease-out;
    }

    .animate-slideUp {
      animation: slideUp 0.3s ease-out;
    }
  `]
})
export class ModalComponent {
  @Input() isOpen: boolean = false;
  @Input() title: string = '';
  @Input() size: ModalSize = 'md';
  @Input() showCloseButton: boolean = true;
  @Input() showIcon: boolean = true;
  @Input() iconType: 'document' | 'edit' | 'delete' | 'info' | 'warning' | 'success' | 'custom' = 'document';
  @Input() customIconPath: string = '';
  @Input() closeOnBackdrop: boolean = true;
  @Input() showFooter: boolean = false;

  // Colores del header
  @Input() gradientFrom: string = 'from-blue-600';
  @Input() gradientTo: string = 'to-indigo-700';

  @Output() modalClose = new EventEmitter<void>();

  close(): void {
    this.modalClose.emit();
  }

  onBackdropClick(event: MouseEvent): void {
    if (this.closeOnBackdrop) {
      this.close();
    }
  }

  getModalClasses(): string {
    const baseClasses = 'bg-white rounded-2xl shadow-2xl w-full overflow-hidden animate-slideUp';

    const sizeClasses: Record<ModalSize, string> = {
      'sm': 'max-w-sm',
      'md': 'max-w-lg',
      'lg': 'max-w-2xl',
      'xl': 'max-w-4xl',
      '2xl': 'max-w-6xl'
    };

    return `${baseClasses} ${sizeClasses[this.size]}`;
  }

  getIconPath(): string {
    if (this.iconType === 'custom' && this.customIconPath) {
      return this.customIconPath;
    }

    const icons: Record<string, string> = {
      'document': 'M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z',
      'edit': 'M11 5H6a2 2 0 00-2 2v11a2 2 0 002 2h11a2 2 0 002-2v-5m-1.414-9.414a2 2 0 112.828 2.828L11.828 15H9v-2.828l8.586-8.586z',
      'delete': 'M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16',
      'info': 'M13 16h-1v-4h-1m1-4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z',
      'warning': 'M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-3L13.732 4c-.77-1.333-2.694-1.333-3.464 0L3.34 16c-.77 1.333.192 3 1.732 3z',
      'success': 'M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z'
    };

    return icons[this.iconType] || icons['document'];
  }
}