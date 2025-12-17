import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HeaderComponent } from '../../../shared/header/header.component';
import { SidebarComponent } from '../../../shared/sidebar/sidebar.component';
import { OrdenService } from '../services/orden.service';
import { ExportService } from '../services/export.service';
import { Orden, OrdenFilter } from '../models/orden.model';
import { DetallesExamenModalComponent } from './detalles-examen-modal.component';
import { ProcesarOrdenModalComponent } from './procesar-orden-modal.component';

@Component({
    selector: 'app-atenciones',
    standalone: true,
    imports: [CommonModule, FormsModule, HeaderComponent, SidebarComponent, DetallesExamenModalComponent, ProcesarOrdenModalComponent],
    templateUrl: './atenciones.component.html',
    styleUrl: './atenciones.component.css'
})
export class AtencionesComponent implements OnInit {

    ordenes: Orden[] = [];
    ordenesFiltradas: Orden[] = [];
    cargando: boolean = false;
    error: string | null = null;

    // Filtros
    filtro: OrdenFilter = {};
    fechaInicio: string = '';
    fechaFin: string = '';
    dniPaciente: string = '';
    estadoSelected: string = '';

    // Paginación
    paginaActual: number = 1;
    itemsPorPagina: number = 10;
    totalPaginas: number = 1;

    // Modales
    mostrarModalDetalles: boolean = false;
    mostrarModalProcesar: boolean = false;
    ordenSeleccionada: Orden | null = null;

    // Notificaciones
    notificaciones: string[] = [];

    // Para usar Math en template
    Math = Math;

    constructor(
        private ordenService: OrdenService,
        private exportService: ExportService,
        private cdr: ChangeDetectorRef
    ) { }

    ngOnInit() {
        console.log('🚀 OnInit: Iniciando carga automática...');
        this.cargarOrdenes();
        this.ordenesFiltradas = [];
    }

    cargarOrdenes() {
        console.log('🔄 cargarOrdenes: Inicio');
        this.cargando = true;
        this.error = null;

        this.ordenService.obtenerTodasLasOrdenes().subscribe({
            next: (data) => {
                try {
                    console.log('✅ Next: Data recibida', data?.length);

                    if (!data || data.length === 0) {
                        console.warn('⚠️ Data vacía');
                    }

                    this.ordenes = data || [];
                    this.aplicarFiltros();
                    console.log('📊 Filtros aplicados. Filtradas:', this.ordenesFiltradas.length);

                } catch (error) {
                    console.error('❌ Error en try block:', error);
                    this.error = 'Error al procesar los datos recibidos.';
                } finally {
                    console.log('🏁 Finally: Desactivando spinner');
                    this.cargando = false;
                    this.cdr.detectChanges(); // Force update
                }
            },
            error: (err) => {
                console.error('❌ Error en subscribe:', err);
                this.error = 'Error al cargar las órdenes. Por favor intente nuevamente.';
                this.cargando = false;
                this.cdr.detectChanges();
            }
        });
    }

    buscar() {
        // Cargar datos del backend
        this.cargarOrdenes();
    }

    aplicarFiltros() {
        let resultado = [...this.ordenes];

        // Filtro por fechas
        if (this.fechaInicio) {
            const fechaInicioDate = new Date(this.fechaInicio);
            resultado = resultado.filter(orden => {
                const fechaOrden = new Date(orden.fechaOrden);
                // Comparar solo fechas ignorando horas
                fechaOrden.setHours(0, 0, 0, 0);
                const inicio = new Date(fechaInicioDate);
                inicio.setHours(0, 0, 0, 0)
                // Permitir una pequeña tolerancia por zona horaria
                // Mejor usar strings ISO YYYY-MM-DD para evitar rollo de zonas
                return orden.fechaOrden.substring(0, 10) >= this.fechaInicio;
            });
        }

        if (this.fechaFin) {
            // Comparar strings YYYY-MM-DD es más seguro y fácil
            resultado = resultado.filter(orden => {
                return orden.fechaOrden.substring(0, 10) <= this.fechaFin;
            });
        }

        // Filtro por estado
        if (this.estadoSelected && this.estadoSelected !== '' && this.estadoSelected !== 'Todos') {
            resultado = resultado.filter(orden => orden.estado === this.estadoSelected);
        }

        // Filtro por DNI o nombre de paciente
        if (this.dniPaciente.trim()) {
            const busqueda = this.dniPaciente.trim().toLowerCase();
            resultado = resultado.filter(orden =>
                orden.patientDni.toLowerCase().includes(busqueda) ||
                orden.patientFirstName.toLowerCase().includes(busqueda) ||
                orden.patientLastName.toLowerCase().includes(busqueda)
            );
        }

        this.ordenesFiltradas = resultado;
        this.calcularPaginacion();
    }

    calcularPaginacion() {
        this.totalPaginas = Math.ceil(this.ordenesFiltradas.length / this.itemsPorPagina);
        if (this.paginaActual > this.totalPaginas && this.totalPaginas > 0) {
            this.paginaActual = this.totalPaginas;
        }
    }

    get ordenesPaginadas(): Orden[] {
        const inicio = (this.paginaActual - 1) * this.itemsPorPagina;
        const fin = inicio + this.itemsPorPagina;
        return this.ordenesFiltradas.slice(inicio, fin);
    }

    irAPagina(pagina: number) {
        if (pagina >= 1 && pagina <= this.totalPaginas) {
            this.paginaActual = pagina;
        }
    }

    get paginasArray(): number[] {
        return Array.from({ length: this.totalPaginas }, (_, i) => i + 1);
    }


    // MODALES
    verDetalles(ordenId: number) {
        const orden = this.ordenes.find(o => o.id === ordenId);
        if (orden) {
            this.ordenSeleccionada = orden;
            this.mostrarModalDetalles = true;
        }
    }

    cerrarModalDetalles() {
        this.mostrarModalDetalles = false;
        this.ordenSeleccionada = null;
    }

    procesarOrden(ordenId: number) {
        const orden = this.ordenes.find(o => o.id === ordenId);
        if (orden) {
            this.ordenSeleccionada = orden;
            this.mostrarModalProcesar = true;
        }
    }

    cerrarModalProcesar() {
        this.mostrarModalProcesar = false;
        this.ordenSeleccionada = null;
    }

    verResultados() {
        console.log('Ver resultados de orden:', this.ordenSeleccionada?.id);
        this.mostrarNotificacion('Funcionalidad "Ver Resultados" pendiente de implementación');
    }

    verExamenes() {
        console.log('Ver exámenes de orden:', this.ordenSeleccionada?.id);
        this.mostrarNotificacion('Funcionalidad "Ver Exámenes" pendiente de implementación');
    }

    // EXPORTACIONES
    exportarPDF() {
        this.exportService.exportarAPDF(this.ordenesFiltradas);
        this.mostrarNotificacion('Reporte PDF generado exitosamente');
    }

    exportarExcel() {
        this.exportService.exportarAExcel(this.ordenesFiltradas);
        this.mostrarNotificacion('Reporte Excel generado exitosamente');
    }

    // NOTIFICACIONES
    mostrarNotificacion(mensaje: string) {
        this.notificaciones.push(mensaje);
        setTimeout(() => {
            this.notificaciones.shift();
        }, 3000);
    }

    // UTILIDADES
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

    getEstadoClass(estado: string): string {
        switch (estado) {
            case 'PENDIENTE': return 'bg-yellow-100 text-yellow-800';
            case 'EN_PROCESO': return 'bg-blue-100 text-blue-800';
            case 'PROCESADO': return 'bg-purple-100 text-purple-800';
            case 'VALIDADO': return 'bg-green-100 text-green-800';
            case 'ENTREGADO': return 'bg-gray-100 text-gray-800';
            default: return 'bg-gray-100 text-gray-800';
        }
    }

    cerrarSesion() {
        console.log('Cerrando sesión desde reportes atenciones');
    }
}
