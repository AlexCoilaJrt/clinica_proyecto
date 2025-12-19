import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { CdkDragDrop, DragDropModule, moveItemInArray } from '@angular/cdk/drag-drop';
import { SubExam, SubExamRequest, TipoResultado } from '../../../models/exam.model';
import { ExamService } from '../../../services/exam.service';
import { HeaderComponent } from "../../../shared/header/header.component";
import { SidebarComponent } from "../../../shared/sidebar/sidebar.component";
import { Columna } from "../../../shared/components/tabla-general/tabla-general.component";

@Component({
    selector: 'app-sub-examenes',
    standalone: true,
    imports: [CommonModule, FormsModule, DragDropModule, HeaderComponent, SidebarComponent],
    templateUrl: './sub-examenes.component.html',
    styleUrls: ['./sub-examenes.component.css']
})
export class SubExamenesComponent implements OnInit {
    subExams: SubExam[] = [];

    currentPage: number = 0;
    examenId: number | null = null;
    examenNombre: string = '';
    columnassubexam: Columna[] = [
        { field: 'id', header: 'N°', tipo: 'index', subField: ['areaCodigo'] },
        { field: 'nombre', header: 'NOMBRE', tipo: 'text', subField: ['areaCodigo'] },
        { field: 'codigo', header: 'Código', tipo: 'badge', subField: ['areaCodigo'] },
        { field: 'getTipoResultadoLabel(subExam.tipoResultado)', header: 'TIPO', tipo: 'area-badge', subField: ['areaCodigo'] },
        { field: 'unidadMedida', header: 'UNIDAD', tipo: 'currency', subField: ['areaCodigo'] },
        { field: 'createdAt', header: 'Fecha de Creación', tipo: 'date', subField: ['areaCodigo'] }
    ];

    tiposResultado = Object.values(TipoResultado);


    showModal: boolean = false;
    isEditMode: boolean = false;
    selectedSubExam: SubExam | null = null;


    formData: SubExamRequest = this.getEmptyForm();

    constructor(
        private examService: ExamService,
        private route: ActivatedRoute,
        private router: Router,
        private cdRef: ChangeDetectorRef
    ) { }

    ngOnInit(): void {

        this.route.params.subscribe(params => {
            this.examenId = params['id'] ? +params['id'] : null;
            this.examenNombre = params['examenNombre'] || '';
            console.log('Cargando subexámenes para examen ID:', this.examenId);
            if (this.examenId) {
                this.loadSubExams();
            }
        });
    }

    loadSubExams(): void {
        if (!this.examenId) return;

        this.examService.getSubExamsByExam(this.examenId).subscribe({
            next: (response) => {
                if (response.success && response.data) {
                    this.subExams = response.data;
                    console.log('Subexámenes cargados:', this.subExams);
                    this.cdRef.detectChanges();
                }
            },
            error: (error) => {
                console.error('Error al cargar subexámenes:', error);
                alert('Error al cargar los subexámenes');
            }
        });
    }

    openCreateModal(): void {
        this.isEditMode = false;
        this.selectedSubExam = null;
        this.formData = this.getEmptyForm();
        if (this.examenId) {
            this.formData.examenId = this.examenId;

            this.formData.ordenVisualizacion = this.subExams.length + 1;
        }
        this.showModal = true;
    }

    openEditModal(subExam: SubExam): void {
        this.isEditMode = true;
        this.selectedSubExam = subExam;
        this.formData = {
            examenId: subExam.examenId,
            codigo: subExam.codigo,
            nombre: subExam.nombre,
            tipoResultado: subExam.tipoResultado,
            unidadMedida: subExam.unidadMedida,
            valorMinimo: subExam.valorMinimo,
            valorMaximo: subExam.valorMaximo,
            valorCriticoMin: subExam.valorCriticoMin,
            valorCriticoMax: subExam.valorCriticoMax,
            ordenVisualizacion: subExam.ordenVisualizacion,
            observaciones: subExam.observaciones
        };
        this.showModal = true;
    }

    closeModal(): void {
        this.showModal = false;
        this.selectedSubExam = null;
        this.formData = this.getEmptyForm();
    }

    saveSubExam(): void {
        if (!this.formData.codigo || !this.formData.nombre || !this.formData.examenId) {
            alert('Por favor complete los campos obligatorios');
            return;
        }

        if (this.isEditMode && this.selectedSubExam) {

            this.examService.updateSubExam(this.selectedSubExam.id, this.formData).subscribe({
                next: (response) => {
                    if (response.success) {
                        alert('Subexamen actualizado exitosamente');
                        this.closeModal();
                        this.loadSubExams();
                    }
                },
                error: (error) => {
                    console.error('Error al actualizar:', error);
                    alert(error.error?.message || 'Error al actualizar el subexamen');
                }
            });
        } else {

            this.examService.createSubExam(this.formData).subscribe({
                next: (response) => {
                    if (response.success) {
                        alert('Subexamen creado exitosamente');
                        this.closeModal();
                        this.loadSubExams();
                    }
                },
                error: (error) => {
                    console.error('Error al crear:', error);
                    alert(error.error?.message || 'Error al crear el subexamen');
                }
            });
        }
    }

    deleteSubExam(subExam: SubExam): void {
        if (confirm(`¿Está seguro de eliminar el subexamen "${subExam.nombre}"?`)) {
            this.examService.deleteSubExam(subExam.id).subscribe({
                next: (response) => {
                    if (response.success) {
                        alert('Subexamen eliminado exitosamente');
                        this.loadSubExams();
                    }
                },
                error: (error) => {
                    console.error('Error al eliminar:', error);
                    alert('Error al eliminar el subexamen');
                }
            });
        }
    }


    drop(event: CdkDragDrop<SubExam[]>): void {
        moveItemInArray(this.subExams, event.previousIndex, event.currentIndex);


        const subExamIds = this.subExams.map(se => se.id);

        if (this.examenId) {
            this.examService.reorderSubExams(this.examenId, subExamIds).subscribe({
                next: (response) => {
                    if (response.success) {
                        console.log('Orden actualizado exitosamente');
                        this.loadSubExams();
                    }
                },
                error: (error) => {
                    console.error('Error al reordenar:', error);
                    alert('Error al actualizar el orden');
                    this.loadSubExams();
                }
            });
        }
    }

    goBack(): void {
        this.router.navigate(['/examenes'], {
            queryParams: {
                areaId: this.route.snapshot.queryParams['areaId'],
                areaCodigo: this.route.snapshot.queryParams['areaCodigo']
            }
        });
    }

    private getEmptyForm(): SubExamRequest {
        return {
            examenId: 0,
            codigo: '',
            nombre: '',
            tipoResultado: TipoResultado.NUMERICO,
            ordenVisualizacion: 1
        };
    }

    getTipoResultadoLabel(tipo: TipoResultado): string {
        const labels: { [key in TipoResultado]: string } = {
            [TipoResultado.NUMERICO]: 'Numérico',
            [TipoResultado.TEXTO]: 'Texto',
            [TipoResultado.IMAGEN]: 'Imagen',
            [TipoResultado.PANEL]: 'Panel',
            [TipoResultado.CUANTITATIVO]: 'Cuantitativo',
            [TipoResultado.CUALITATIVO]: 'Cualitativo'
        };
        return labels[tipo] || tipo;
    }
}