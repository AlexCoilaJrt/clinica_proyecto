import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { AuditService, AuditLog } from '../../services/audit.service';
import { HeaderComponent } from '../../../../shared/header/header.component';
import { SidebarComponent } from '../../../../shared/sidebar/sidebar.component';



@Component({
    selector: 'app-bitacora',
    standalone: true,
    imports: [CommonModule, FormsModule, RouterModule, HeaderComponent, SidebarComponent],
    templateUrl: './bitacora.component.html'
})
export class BitacoraComponent implements OnInit {
    logs: AuditLog[] = [];
    filteredLogs: AuditLog[] = []; // Para paginación local o resultados
    paginatedLogs: AuditLog[] = []; // Logs que se muestran en la página actual
    filters = {
        username: '',
        action: 'Todas',
        startDate: '',
        endDate: ''
    };

    // Paginación
    currentPage = 1;
    itemsPerPage = 10;
    totalPages = 1;

    // Modal
    selectedLog: AuditLog | null = null;

    constructor(
        private auditService: AuditService,
        private cdr: ChangeDetectorRef
    ) { }

    ngOnInit() {
        console.log('BitacoraComponent initialized');
        this.search();
    }

    // Mapeo de colores para status
    getStatusClass(status: string): string {
        return status === 'Éxito'
            ? 'bg-blue-100 text-blue-800'
            : 'bg-red-100 text-red-800';
    }

    // Mapeo de colores para acciones
    getActionClass(action: string): string {
        const classes: { [key: string]: string } = {
            'INSERT': 'bg-green-100 text-green-800',
            'UPDATE': 'bg-yellow-100 text-yellow-800',
            'DELETE': 'bg-red-100 text-red-800',
            'LOGIN': 'bg-cyan-100 text-cyan-800',
            'LOGIN_SUCCESS': 'bg-cyan-100 text-cyan-800',
            'LOGIN_FAILED': 'bg-red-100 text-red-800',
            'LOGIN_BLOCKED_IP': 'bg-purple-100 text-purple-800',

            // Acciones de Usuario
            'USER_CREATE': 'bg-green-100 text-green-800',
            'USER_UPDATE': 'bg-yellow-100 text-yellow-800',
            'USER_DELETE': 'bg-red-100 text-red-800',
            'USER_STATUS_CHANGE': 'bg-purple-100 text-purple-800',
            'USER_ROLE_ASSIGN': 'bg-blue-100 text-blue-800',
            'USER_ROLE_REMOVE': 'bg-orange-100 text-orange-800',
            'USER_PASSWORD_CHANGE': 'bg-indigo-100 text-indigo-800'
        };
        return classes[action] || 'bg-gray-100 text-gray-800';
    }

    // Mapeo de etiquetas amigables para acciones
    getActionLabel(action: string): string {
        const labels: { [key: string]: string } = {
            'LOGIN': 'Login',
            'LOGIN_SUCCESS': '✅ Login Exitoso',
            'LOGIN_FAILED': '❌ Login Fallido',
            'LOGOUT': '🚪 Cerrar Sesión',
            'INSERT': '➕ Insertar Datos',
            'UPDATE': '📝 Actualizar Registro',
            'DELETE': '🗑️ Eliminar Registro',
            'USER_CREATE': '👤 Crear Usuario',
            'USER_UPDATE': '✏️ Actualizar Usuario',
            'USER_DELETE': '🚫 Eliminar Usuario',
            'USER_STATUS_CHANGE': '🔄 Cambiar Estado',
            'USER_ROLE_ASSIGN': '🎭 Asignar Roles',
            'USER_ROLE_REMOVE': '❌ Remover Roles',
            'USER_PASSWORD_CHANGE': '🔑 Cambiar Contraseña'
        };
        return labels[action] || action;
    }

    search() {
        console.log('Search called with filters:', this.filters);

        const searchFilters = { ...this.filters };
        if (searchFilters.startDate) searchFilters.startDate += 'T00:00:00';
        if (searchFilters.endDate) searchFilters.endDate += 'T23:59:59';

        console.log('Calling API with filters:', searchFilters);

        this.auditService.getLogs(searchFilters).subscribe({
            next: (logs) => {
                console.log('Received logs:', logs);
                this.logs = logs;
                this.updatePagination();
            },
            error: (error) => {
                console.error('Error loading logs:', error);
            }
        });
    }

    clean() {
        this.filters = {
            username: '',
            action: 'Todas',
            startDate: '',
            endDate: ''
        };
        this.search();
    }

    viewDetails(log: AuditLog) {
        this.selectedLog = log;
    }

    closeDetails() {
        this.selectedLog = null;
    }

    // Paginación Simple (Frontend side)
    updatePagination() {
        // Asegurar que itemsPerPage sea número
        this.itemsPerPage = Number(this.itemsPerPage);
        this.currentPage = 1; // Resetear a la primera página
        this.totalPages = Math.ceil(this.logs.length / this.itemsPerPage);
        this.applyPagination();
    }

    applyPagination() {
        const start = (this.currentPage - 1) * this.itemsPerPage;
        const end = start + this.itemsPerPage;
        this.paginatedLogs = this.logs.slice(start, end);
        this.cdr.detectChanges();
    }

    prevPage() {
        if (this.currentPage > 1) {
            this.currentPage--;
            this.applyPagination();
        }
    }

    nextPage() {
        if (this.currentPage < this.totalPages) {
            this.currentPage++;
            this.applyPagination();
        }
    }

    goToPage(page: number) {
        if (page >= 1 && page <= this.totalPages) {
            this.currentPage = page;
            this.applyPagination();
        }
    }
}
