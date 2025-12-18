import { Injectable } from '@angular/core';
import jsPDF from 'jspdf';
import autoTable from 'jspdf-autotable';
import * as XLSX from 'xlsx';
import { saveAs } from 'file-saver';
import { Orden } from '../models/orden.model';

@Injectable({
    providedIn: 'root'
})
export class ExportService {

    constructor() { }

    /**
     * Exporta órdenes a PDF
     */
    exportarAPDF(ordenes: Orden[], nombreArchivo: string = 'reporte_atenciones.pdf') {
        const doc = new jsPDF('l', 'mm', 'a4'); // Landscape para más columnas

        // Título
        doc.setFontSize(18);
        doc.setFont('helvetica', 'bold');
        doc.text('Reporte de Atenciones - Laboratorio Clínico', 14, 15);

        // Fecha de generación
        doc.setFontSize(10);
        doc.setFont('helvetica', 'normal');
        doc.text(`Generado: ${new Date().toLocaleString('es-PE')}`, 14, 22);
        doc.text(`Total de registros: ${ordenes.length}`, 14, 27);

        // Preparar datos para la tabla
        const headers = [
            ['Fecha', 'N° Orden', 'Paciente', 'DNI', 'Estado', 'Total', 'Médico', 'Tipo Atención']
        ];

        const data = ordenes.map(orden => [
            this.formatearFecha(orden.fechaOrden),
            orden.numeroOrden,
            `${orden.patientFirstName} ${orden.patientLastName}`,
            orden.patientDni,
            orden.estado,
            `S/ ${orden.total.toFixed(2)}`,
            orden.medicoNombre || 'N/A',
            orden.tipoAtencion || 'AMBULATORIO'
        ]);

        // Generar tabla
        autoTable(doc, {
            head: headers,
            body: data,
            startY: 32,
            styles: {
                fontSize: 8,
                cellPadding: 2
            },
            headStyles: {
                fillColor: [37, 99, 235], // Azul #2563EB
                textColor: 255,
                fontStyle: 'bold'
            },
            alternateRowStyles: {
                fillColor: [245, 245, 245]
            },
            margin: { top: 32 }
        });

        // Guardar PDF
        doc.save(nombreArchivo);
    }

    /**
     * Exporta órdenes a Excel
     */
    exportarAExcel(ordenes: Orden[], nombreArchivo: string = 'reporte_atenciones.xlsx') {
        // Preparar datos
        const data = ordenes.map(orden => ({
            'Fecha Orden': this.formatearFecha(orden.fechaOrden),
            'N° Orden': orden.numeroOrden,
            'Paciente': `${orden.patientFirstName} ${orden.patientLastName}`,
            'DNI': orden.patientDni,
            'Estado': orden.estado,
            'Prioridad': orden.prioridad,
            'Tipo Atención': orden.tipoAtencion || 'AMBULATORIO',
            'Diagnóstico': orden.diagnostico || '',
            'Médico': orden.medicoNombre || 'N/A',
            'Usuario Registro': orden.userName || '',
            'Total': orden.total,
            'Fecha Registro': this.formatearFecha(orden.createdAt),
            'Observaciones': orden.observaciones || ''
        }));

        // Crear worksheet
        const ws: XLSX.WorkSheet = XLSX.utils.json_to_sheet(data);

        // Ajustar ancho de columnas
        const colWidths = [
            { wch: 18 }, // Fecha Orden
            { wch: 18 }, // N° Orden
            { wch: 30 }, // Paciente
            { wch: 12 }, // DNI
            { wch: 15 }, // Estado
            { wch: 12 }, // Prioridad
            { wch: 15 }, // Tipo Atención
            { wch: 40 }, // Diagnóstico
            { wch: 25 }, // Médico
            { wch: 20 }, // Usuario Registro
            { wch: 12 }, // Total
            { wch: 18 }, // Fecha Registro
            { wch: 40 }  // Observaciones
        ];
        ws['!cols'] = colWidths;

        // Crear workbook
        const wb: XLSX.WorkBook = XLSX.utils.book_new();
        XLSX.utils.book_append_sheet(wb, ws, 'Atenciones');

        // Guardar archivo
        const wbout = XLSX.write(wb, { bookType: 'xlsx', type: 'array' });
        saveAs(new Blob([wbout], { type: 'application/octet-stream' }), nombreArchivo);
    }

    /**
     * Exporta una orden individual con detalles a PDF
     */
    exportarOrdenAPDF(orden: Orden, nombreArchivo?: string) {
        const doc = new jsPDF();

        // Encabezado
        doc.setFillColor(37, 99, 235); // Azul #2563EB
        doc.rect(0, 0, 210, 40, 'F');

        doc.setTextColor(255, 255, 255);
        doc.setFontSize(20);
        doc.setFont('helvetica', 'bold');
        doc.text('ORDEN DE LABORATORIO', 105, 15, { align: 'center' });

        doc.setFontSize(12);
        doc.text(orden.numeroOrden, 105, 25, { align: 'center' });
        doc.text(`Estado: ${orden.estado}`, 105, 32, { align: 'center' });

        // Información del paciente
        doc.setTextColor(0, 0, 0);
        doc.setFontSize(14);
        doc.setFont('helvetica', 'bold');
        doc.text('Información del Paciente', 14, 50);

        doc.setFontSize(10);
        doc.setFont('helvetica', 'normal');
        doc.text(`Nombre: ${orden.patientFirstName} ${orden.patientLastName}`, 14, 58);
        doc.text(`DNI: ${orden.patientDni}`, 14, 64);
        doc.text(`Fecha de orden: ${this.formatearFecha(orden.fechaOrden)}`, 14, 70);

        // Información médica
        doc.setFont('helvetica', 'bold');
        doc.text('Información Médica', 14, 80);

        doc.setFont('helvetica', 'normal');
        doc.text(`Médico: ${orden.medicoNombre || 'No asignado'}`, 14, 88);
        doc.text(`Diagnóstico: ${orden.diagnostico || 'Sin especificar'}`, 14, 94);
        doc.text(`Tipo de atención: ${orden.tipoAtencion || 'AMBULATORIO'}`, 14, 100);

        // Exámenes solicitados
        doc.setFont('helvetica', 'bold');
        doc.text('Exámenes Solicitados', 14, 110);

        const examHeaders = [['#', 'Examen', 'Estado', 'Precio']];
        const examData = orden.detalles.map((det, idx) => [
            (idx + 1).toString(),
            det.examName,
            det.estado,
            `S/ ${det.precio.toFixed(2)}`
        ]);

        autoTable(doc, {
            head: examHeaders,
            body: examData,
            startY: 115,
            styles: { fontSize: 9 },
            headStyles: {
                fillColor: [37, 99, 235],
                textColor: 255
            }
        });

        // Total
        const finalY = (doc as any).lastAutoTable.finalY || 115;
        doc.setFont('helvetica', 'bold');
        doc.setFontSize(12);
        doc.text(`TOTAL: S/ ${orden.total.toFixed(2)}`, 14, finalY + 10);

        // Guardar
        const archivo = nombreArchivo || `orden_${orden.numeroOrden}.pdf`;
        doc.save(archivo);
    }

    /**
     * Formatea fecha para exportación
     */
    private formatearFecha(fecha: string): string {
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
