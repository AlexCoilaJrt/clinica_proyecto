import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { ExamService } from '../../services/exam.service';
import { LabAreaService } from '../../services/lab-area.service';
import { Exam, ExamRequest, TipoResultado, TipoMuestra, PageResponse, ExamType, SubExamRequest } from '../../models/exam.model';
import { LabArea } from '../../models/lab-area.model';
import { HeaderComponent } from "../../shared/header/header.component";
import { SidebarComponent } from "../../shared/sidebar/sidebar.component";
import { BreadcrumbComponent, BreadcrumbItem } from "../../shared/components/breadcrumb/breadcrumb";
import { PageHeaderComponent } from "../../shared/components/page-header/page-header";
import { PrimaryButtonComponent } from "../../shared/components/primary-button/primary-button";
import { Columna, TablaGeneralComponent } from "../../shared/components/tabla-general/tabla-general.component";
import { ModalComponent } from "../../shared/components/modal/modal";
import { PaginationComponent } from "../../shared/components/pagination/pagination";
import { forkJoin } from 'rxjs';

@Component({
  selector: 'app-lab-examenes',
  standalone: true,
  imports: [CommonModule, FormsModule, HeaderComponent, SidebarComponent, BreadcrumbComponent, PageHeaderComponent, PrimaryButtonComponent, TablaGeneralComponent, ModalComponent, PaginationComponent],
  templateUrl: './lab-examenes.component.html',
  styleUrls: ['./lab-examenes.component.css']
})
export class LabExamenesComponent implements OnInit {
  
  breadcrumbItems: BreadcrumbItem[] = [
    { label: 'Configuración', route: '/configuraciones' },
    { label: 'Examenes' }
  ];



  tituloPagina: string = 'Áreas de Laboratorio';
  

  columnasExamenes: Columna[] = [
    { field: 'id', header: 'N°', tipo: 'index', subField: ['areaCodigo'] },
    { field: 'codigo', header: 'Código', tipo: 'badge', subField: ['areaCodigo'] },
    { field: 'nombre', header: 'Examen', tipo: 'nombre-exam', subField: ['tipoResultado', 'metodo', 'tipoMuestra'] },
    { field: 'areaNombre', header: 'Área', tipo: 'area-info', subField: ['areaCodigo'] },
    { field: 'tipoExamenNombre', header: 'Tipo', tipo: 'text', subField: ['areaCodigo'] },
    { field: 'tipoMuestra', header: 'Muestra', tipo: 'text', subField: ['areaCodigo'] },
    { field: 'precio', header: 'Precio', tipo: 'currency', subField: ['areaCodigo'] },
    { field: 'tiempoEntrega', header: 'Entrega', tipo: 'tiempo-entrega', subField: ['areaCodigo'] },
    { field: 'active', header: 'Estado', tipo: 'status', subField: ['areaCodigo'] }
  ];
  
  
  showDetailsModal: boolean = false;
  showEditModal: boolean = false;
  selectedExam: Exam | null = null;

  exams: Exam[] = [];
  currentPage: number = 0;
  pageSize: number = 10;
  totalPages: number = 0;
  totalElements: number = 0;

  
  areaId: number | null = null;
  areaCodigo: string = '';
  areaNombre: string = '';
  public Math = Math;
  
  areas: LabArea[] = [];
  tiposExamen: ExamType[] = [
    { id: 1, nombre: 'CUANTITATIVO', descripcion: 'Examen cuantitativo', active: true },
    { id: 2, nombre: 'CUALITATIVO', descripcion: 'Examen cualitativo', active: true },
    { id: 3, nombre: 'TEXTO', descripcion: 'Resultado en texto', active: true },
    { id: 4, nombre: 'IMAGEN', descripcion: 'Resultado en imagen', active: true },
    { id: 5, nombre: 'PANEL', descripcion: 'Panel o perfil', active: true }
  ];

  tiposResultado = Object.values(TipoResultado);
  tiposMuestra = Object.values(TipoMuestra);

  
  showModal: boolean = false;
  isEditMode: boolean = false;


  
  formData: ExamRequest = this.getEmptyForm();
  searchQuery: string = '';
  
  subExams: SubExamRequest[] = [];
  showSubExamForm: boolean = false;
  editingSubExamIndex: number | null = null;
  subExamForm: SubExamRequest = this.getEmptySubExamForm();
  constructor(
    private examService: ExamService,
    private labAreaService: LabAreaService,
    private route: ActivatedRoute,
    private router: Router,
    private cdRef: ChangeDetectorRef
  ) { }
  
  ngOnInit(): void {
    this.route.params.subscribe(params => {
      this.areaId = params['id'] ? +params['id'] : null;

      console.log('Área ID desde query params:', this.areaId);
      if (this.areaId) {
        this.loadAreaInfo();
        this.loadExams();

        console.log('Área cargada:', this.areaNombre);

      }
    });

    this.loadAreas();
  }

  loadAreaInfo(): void {
    if (!this.areaId) return;

    this.labAreaService.getById(this.areaId).subscribe({
      next: (response) => {
        if (response.success && response.data) {
          this.areaNombre = response.data.descripcion;
          this.areaCodigo = response.data.codigo;
          this.tituloPagina = `Área de ${this.areaNombre}`;
          console.log('Área cargada:', this.areaNombre);
          this.cdRef.detectChanges();
        }
      },
      error: (error) => {
        console.error('Error al cargar área:', error);
      }
    });
  }

  loadAreas(): void {
    this.labAreaService.getAllActive().subscribe({
      next: (response) => {
        if (response.success && response.data) {
          this.areas = response.data;
        }
      },
      error: (error) => {
        console.error('Error al cargar áreas:', error);
      }
    });
  }

  loadExams(): void {
    if (!this.areaId) return;

    if (this.searchQuery.trim()) {
      this.searchExams();
    } else {
      this.examService.getExamsByArea(this.areaId, this.currentPage, this.pageSize).subscribe({
        next: (response) => {
          if (response.success && response.data) {
            this.exams = response.data.content;
            console.log('Exámenes cargados:', this.exams);
            this.totalPages = response.data.totalPages;
            this.totalElements = response.data.totalElements;
            this.currentPage = response.data.number;
            this.cdRef.detectChanges();
          }
        },
        error: (error) => {
          console.error('Error al cargar exámenes:', error);
          alert('Error al cargar los exámenes');
        }
      });
    }
  }

  deleteExam(exam: Exam): void {
    if (confirm(`¿Está seguro de eliminar el examen "${exam.nombre}"?`)) {
      this.examService.deleteExam(exam.id).subscribe({
        next: (response) => {
          if (response.success) {
            alert('Examen eliminado exitosamente');
            this.loadExams();
          }
        },
        error: (error) => {
          console.error('Error al eliminar:', error);
          alert('Error al eliminar el examen');
        }
      });
    }
  }



  searchExams(): void {
    this.examService.searchExams(this.searchQuery, this.currentPage, this.pageSize).subscribe({
      next: (response) => {
        if (response.success && response.data) {
          this.exams = response.data.content;
          this.totalPages = response.data.totalPages;
          this.totalElements = response.data.totalElements;
        }
      },
      error: (error) => {
        console.error('Error en búsqueda:', error);
      }
    });
  }
  openCreateModal(): void {
    this.isEditMode = false;
    this.selectedExam = null;
    this.formData = this.getEmptyForm();
    if (this.areaId) {
      this.formData.areaId = this.areaId;
    }
    this.showModal = true;
  }
  openEditModal(exam: Exam): void {
    this.selectedExam = exam;
    this.showEditModal = true;

    this.formData = {
      areaId: exam.areaId,
      codigo: exam.codigo,
      nombre: exam.nombre,
      metodo: exam.metodo,
      indicaciones: exam.indicaciones,
      precio: exam.precio,
      tiempoEntrega: exam.tiempoEntrega,
      tipoExamenId: exam.tipoExamenId,
      tipoMuestra: exam.tipoMuestra,
      tipoResultado: exam.tipoResultado,
      unidadMedida: exam.unidadMedida,
      esPerfil: exam.esPerfil,
      valorMinimo: exam.valorMinimo,
      valorMaximo: exam.valorMaximo,
      valorCriticoMin: exam.valorCriticoMin,
      valorCriticoMax: exam.valorCriticoMax
    };

    this.showModal = true;
  }
  closeModal(): void {
    this.showModal = false;
    this.isEditMode = false;
    this.selectedExam = null;
    this.formData = this.getEmptyForm();
    this.subExams = [];
    this.showSubExamForm = false;
    this.subExamForm = this.getEmptySubExamForm();
  }

  changePage(page: number): void {
    this.currentPage = page;
    this.loadAreas();
  }
  changePageSize(newSize: number): void {
    this.pageSize = newSize;
    this.currentPage = 0;
    this.loadExams();
  }
  private getEmptyForm(): ExamRequest {
    return {
      codigo: '',
      nombre: '',
      areaId: 0,
      tipoExamenId: 1,
      tipoResultado: TipoResultado.NUMERICO,
      esPerfil: false
    };
  }
  
  getEmptySubExamForm(): SubExamRequest {
    return {
      examenId: 0,
      codigo: '',
      nombre: '',
      tipoResultado: null as any as TipoResultado,
      unidadMedida: '',
      ordenVisualizacion: this.subExams.length + 1,
      valorMinimo: undefined,
      valorMaximo: undefined,
      valorCriticoMin: undefined,
      valorCriticoMax: undefined,
      observaciones: ''
    };
  }
  saveSubExamToList(): void {
    if (!this.subExamForm.codigo || !this.subExamForm.nombre || !this.subExamForm.tipoResultado) {
      alert('Por favor complete los campos obligatorios del subexamen (Código, Nombre, Tipo de Resultado)');
      return;
    }

    if (this.editingSubExamIndex !== null) {
      
      this.subExams[this.editingSubExamIndex] = { ...this.subExamForm };
    } else {
      
      this.subExams.push({ ...this.subExamForm });
    }

    this.cancelSubExam();
  }

  
  cancelSubExam(): void {
    this.showSubExamForm = false;
    this.editingSubExamIndex = null;
    this.subExamForm = this.getEmptySubExamForm();
  }

  
  removeSubExam(index: number): void {
    if (confirm('¿Está seguro de eliminar este subexamen?')) {
      this.subExams.splice(index, 1);
      
      this.subExams.forEach((se, i) => {
        se.ordenVisualizacion = i + 1;
      });
    }
  }

  
  saveExam(): void {
    if (!this.formData.codigo || !this.formData.nombre || !this.formData.areaId || !this.formData.tipoExamenId) {
      alert('Por favor complete los campos obligatorios');
      return;
    }

    
    if (this.formData.esPerfil && this.subExams.length === 0) {
      alert('Debe agregar al menos un subexamen si marca como perfil');
      return;
    }

    if (this.isEditMode && this.selectedExam) {
      
      this.examService.updateExam(this.selectedExam.id, this.formData).subscribe({
        next: (response) => {
          if (response.success) {
            
            if (this.formData.esPerfil && this.subExams.length > 0) {
              this.saveSubExams(this.selectedExam!.id);
            } else {
              alert('Examen actualizado exitosamente');
              this.closeModal();
              this.loadExams();
            }
          }
        },
        error: (error) => {
          console.error('Error al actualizar:', error);
          alert(error.error?.message || 'Error al actualizar el examen');
        }
      });
    } else {
      
      this.examService.createExam(this.formData).subscribe({
        next: (response) => {
          if (response.success && response.data) {
            const examId = response.data.id;

            
            if (this.formData.esPerfil && this.subExams.length > 0) {
              this.saveSubExams(examId);
            } else {
              alert('Examen creado exitosamente');
              this.closeModal();
              this.loadExams();
            }
          }
        },
        error: (error) => {
          console.error('Error al crear:', error);
          alert(error.error?.message || 'Error al crear el examen');
        }
      });
    }
  }

  
  saveSubExams(examId: number): void {
    const subExamRequests = this.subExams.map(subExam => {
      const request: SubExamRequest = {
        examenId: examId,
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
      
      return this.examService.createSubExam(request);
    });

    
    forkJoin(subExamRequests).subscribe({
      next: (responses) => {
        const allSuccess = responses.every((r: any) => r.success);
        if (allSuccess) {
          alert(`Examen y ${this.subExams.length} subexámenes guardados exitosamente`);
          this.closeModal();
          this.loadExams();
        } else {
          alert('Algunos subexámenes no se pudieron guardar');
        }
      },
      error: (error) => {
        console.error('Error al guardar subexámenes:', error);
        alert('Error al guardar los subexámenes');
      }
    });
  }

  viewSubExams(exam: Exam): void {
    if (!exam.esPerfil) {
      console.log('El examen no es un perfil/panel, no tiene subexámenes.');
      alert('Este examen no es un perfil/panel');
      return;
    }
    console.log('Navegando a subexámenes del examen ID:', exam.id);
    this.router.navigate(['configuraciones/sub-examenes', exam.id]);
  }
  
  loadSubExams(examId: number): void {
    
    this.examService.getSubExamsByExam(examId).subscribe({
      next: (response) => {
        if (response.success && response.data) {
          this.subExams = response.data.map((se: any) => ({
            id: se.id,
            examenId: se.examenId,
            codigo: se.codigo,
            nombre: se.nombre,
            tipoResultado: se.tipoResultado,
            unidadMedida: se.unidadMedida,
            valorMinimo: se.valorMinimo,
            valorMaximo: se.valorMaximo,
            valorCriticoMin: se.valorCriticoMin,
            valorCriticoMax: se.valorCriticoMax,
            ordenVisualizacion: se.ordenVisualizacion,
            observaciones: se.observaciones
          }));
        }
      },
      error: (error) => {
        console.error('Error al cargar subexámenes:', error);
      }
    });
  }
  
  addSubExam(): void {
    this.showSubExamForm = true;
    this.editingSubExamIndex = null;
    this.subExamForm = this.getEmptySubExamForm();
  }

  
  editSubExam(index: number): void {
    this.editingSubExamIndex = index;
    this.subExamForm = { ...this.subExams[index] };
    this.showSubExamForm = true;
  }




  getTipoMuestraLabel(tipo: TipoMuestra): string {
    const labels: { [key in TipoMuestra]: string } = {
      [TipoMuestra.SANGRE]: 'Sangre',
      [TipoMuestra.SUERO]: 'Suero',
      [TipoMuestra.PLASMA]: 'Plasma',
      [TipoMuestra.ORINA]: 'Orina',
      [TipoMuestra.HECES]: 'Heces',
      [TipoMuestra.ESPUTO]: 'Esputo',
      [TipoMuestra.LIQUIDO_CEFALORRAQUIDEO]: 'Líquido Cefalorraquídeo',
      [TipoMuestra.OTROS]: 'Otros'
    };
    return labels[tipo] || tipo;
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

  

  viewDetails(exam: Exam): void {
    this.selectedExam = exam;
    this.showDetailsModal = true;
  }

  closeDetailsModal(): void {
    this.showDetailsModal = false;
    this.selectedExam = null;
  }
  





}