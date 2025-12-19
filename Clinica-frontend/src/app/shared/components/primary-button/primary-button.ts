import { Component, Input, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';

type ButtonSize = 'sm' | 'md' | 'lg';
type ButtonColor = 'blue' | 'indigo' | 'green' | 'red' | 'purple' | 'gray';

@Component({
  selector: 'app-primary-button',
  standalone: true,
  imports: [CommonModule],
  template: `
    <button 
      (click)="handleClick()"
      [disabled]="disabled"
      [class]="getButtonClasses()"
      [type]="type">
      
      <!-- Icono -->
      <svg *ngIf="icon" 
           [class]="getIconClasses()"
           fill="none" 
           stroke="currentColor" 
           viewBox="0 0 24 24">
        <path stroke-linecap="round" 
              stroke-linejoin="round" 
              stroke-width="2.5"
              [attr.d]="getIconPath()" />
      </svg>
      
      <!-- Texto -->
      <span>{{ label }}</span>
    </button>
  `
})
export class PrimaryButtonComponent {
  @Input() label: string = 'Botón';
  @Input() icon: 'plus' | 'edit' | 'delete' | 'save' | 'cancel' | 'search' | 'download' | 'upload' | null = null;
  @Input() size: ButtonSize = 'md';
  @Input() color: ButtonColor = 'blue';
  @Input() disabled: boolean = false;
  @Input() fullWidth: boolean = false;
  @Input() type: 'button' | 'submit' | 'reset' = 'button';
  
  @Output() btnClick = new EventEmitter<void>();

  handleClick(): void {
    if (!this.disabled) {
      this.btnClick.emit();
    }
  }

  getButtonClasses(): string {
    const baseClasses = 'group flex items-center gap-3 font-semibold rounded-xl transition-all duration-300 shadow-lg hover:shadow-xl transform hover:-translate-y-0.5';
    
    const sizeClasses: Record<ButtonSize, string> = {
      'sm': 'px-4 py-2 text-sm',
      'md': 'px-8 py-3.5',
      'lg': 'px-10 py-4 text-lg'
    };

    const colorClasses: Record<ButtonColor, string> = {
      'blue': 'bg-gradient-to-r from-blue-600 to-indigo-600 hover:from-blue-700 hover:to-indigo-700 text-white',
      'indigo': 'bg-gradient-to-r from-indigo-600 to-purple-600 hover:from-indigo-700 hover:to-purple-700 text-white',
      'green': 'bg-gradient-to-r from-green-600 to-emerald-600 hover:from-green-700 hover:to-emerald-700 text-white',
      'red': 'bg-gradient-to-r from-red-600 to-rose-600 hover:from-red-700 hover:to-rose-700 text-white',
      'purple': 'bg-gradient-to-r from-purple-600 to-pink-600 hover:from-purple-700 hover:to-pink-700 text-white',
      'gray': 'bg-gradient-to-r from-gray-600 to-slate-600 hover:from-gray-700 hover:to-slate-700 text-white'
    };

    const disabledClasses = this.disabled ? 'opacity-50 cursor-not-allowed hover:shadow-lg hover:translate-y-0' : '';
    const widthClass = this.fullWidth ? 'w-full justify-center' : '';

    return `${baseClasses} ${sizeClasses[this.size]} ${colorClasses[this.color]} ${disabledClasses} ${widthClass}`;
  }

  getIconClasses(): string {
    const sizeClasses: Record<ButtonSize, string> = {
      'sm': 'w-4 h-4',
      'md': 'w-6 h-6',
      'lg': 'w-7 h-7'
    };

    const animationClass = this.icon === 'plus' ? 'group-hover:rotate-90' : '';
    
    return `${sizeClasses[this.size]} ${animationClass} transition-transform duration-300`;
  }

  getIconPath(): string {
    const icons: Record<string, string> = {
      'plus': 'M12 4v16m8-8H4',
      'edit': 'M11 5H6a2 2 0 00-2 2v11a2 2 0 002 2h11a2 2 0 002-2v-5m-1.414-9.414a2 2 0 112.828 2.828L11.828 15H9v-2.828l8.586-8.586z',
      'delete': 'M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16',
      'save': 'M8 7H5a2 2 0 00-2 2v9a2 2 0 002 2h14a2 2 0 002-2V9a2 2 0 00-2-2h-3m-1 4l-3 3m0 0l-3-3m3 3V4',
      'cancel': 'M6 18L18 6M6 6l12 12',
      'search': 'M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z',
      'download': 'M4 16v1a3 3 0 003 3h10a3 3 0 003-3v-1m-4-4l-4 4m0 0l-4-4m4 4V4',
      'upload': 'M4 16v1a3 3 0 003 3h10a3 3 0 003-3v-1m-4-8l-4-4m0 0L8 8m4-4v12'
    };

    return icons[this.icon || ''] || '';
  }
}