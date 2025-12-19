import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { LabAreaService } from '../../services/lab-area.service';
import { LabArea } from '../../models/lab-area.model';
import { HeaderComponent } from "../../shared/header/header.component";
import { SidebarComponent } from "../../shared/sidebar/sidebar.component";
import { Columna, TablaGeneralComponent } from "../../shared/components/tabla-general/tabla-general.component";
import { BreadcrumbComponent, BreadcrumbItem } from '../../shared/components/breadcrumb/breadcrumb';
import { PageHeaderComponent } from '../../shared/components/page-header/page-header';
import { PrimaryButtonComponent } from '../../shared/components/primary-button/primary-button';
import { PaginationComponent } from '../../shared/components/pagination/pagination';
import { ModalComponent } from '../../shared/components/modal/modal';


@Component({
  selector: 'app-lab-areas',
  standalone: true,
  imports: [
    CommonModule, 
    FormsModule, 
    HeaderComponent, 
    SidebarComponent, 
    TablaGeneralComponent,
    BreadcrumbComponent,
    PageHeaderComponent,
    PrimaryButtonComponent,
    PaginationComponent,
    ModalComponent
  ],
  templateUrl: './lab-areas.component.html',
  styleUrls: ['./lab-areas.component.css']
})
export class LabAreasComponent implements OnInit {
  areas: LabArea[] = [];
  currentPage: number = 0;
  pageSize: number = 10;
  totalPages: number = 0;
  totalElements: number = 0;

  
  showModal: boolean = false;
  isEditMode: boolean = false;
  selectedArea: LabArea | null = null;

  
  formData = {
    codigo: '',
    descripcion: ''
  };

  
  breadcrumbItems: BreadcrumbItem[] = [
    { label: 'Configuración', route: '/configuraciones' },
    { label: 'Áreas de Laboratorio' }
  ];

  
  columnasAreas: Columna[] = [
    { field: 'id', header: 'N°', tipo: 'index' ,subField:['areaCodigo']},
    { field: 'codigo', header: 'Código', tipo: 'badge' ,subField:['areaCodigo']},
    { field: 'descripcion', header: 'Descripción del Área', tipo: 'area-badge' ,subField:['areaCodigo']},
    { field: 'createdAt', header: 'Fecha de Creación', tipo: 'date' ,subField:['areaCodigo']}
  ];

  constructor(
    private labAreaService: LabAreaService,
    private router: Router,
    private cdr: ChangeDetectorRef
  ) { }

  ngOnInit(): void {
    setTimeout(() => {
      this.loadAreas();
    });
  }

  loadAreas(): void {
    this.labAreaService.getAll(this.currentPage, this.pageSize).subscribe({
      next: (response) => {
        if (response.success && response.data) {
          this.areas = response.data.content;
          this.totalPages = response.data.totalPages;
          this.totalElements = response.data.totalElements;
          this.currentPage = response.data.number;
          this.cdr.detectChanges();
        }
      },
      error: (error) => {
        console.error('Error al cargar áreas:', error);
        alert('Error al cargar las áreas de laboratorio');
      }
    });
  }

  openCreateModal(): void {
    this.isEditMode = false;
    this.selectedArea = null;
    this.formData = { codigo: '', descripcion: '' };
    this.showModal = true;
  }

  openEditModal(area: LabArea): void {
    this.isEditMode = true;
    this.selectedArea = area;
    this.formData = {
      codigo: area.codigo,
      descripcion: area.descripcion
    };
    this.showModal = true;
  }

  closeModal(): void {
    this.showModal = false;
    this.selectedArea = null;
    this.formData = { codigo: '', descripcion: '' };
  }

  saveArea(): void {
    if (!this.formData.codigo || !this.formData.descripcion) {
      alert('Por favor complete todos los campos');
      return;
    }

    const request = {
      codigo: this.formData.codigo.toUpperCase(),
      descripcion: this.formData.descripcion.toUpperCase()
    };

    if (this.isEditMode && this.selectedArea) {
      this.labAreaService.update(this.selectedArea.id, request).subscribe({
        next: (response) => {
          if (response.success) {
            alert('Área actualizada exitosamente');
            this.closeModal();
            this.loadAreas();
          }
        },
        error: (error) => {
          console.error('Error al actualizar:', error);
          alert(error.error?.message || 'Error al actualizar el área');
        }
      });
    } else {
      this.labAreaService.create(request).subscribe({
        next: (response) => {
          if (response.success) {
            alert('Área creada exitosamente');
            this.closeModal();
            this.loadAreas();
          }
        },
        error: (error) => {
          console.error('Error al crear:', error);
          alert(error.error?.message || 'Error al crear el área');
        }
      });
    }
  }

  deleteArea(area: LabArea): void {
    if (confirm(`¿Está seguro de eliminar el área "${area.descripcion}"?`)) {
      this.labAreaService.delete(area.id).subscribe({
        next: (response) => {
          if (response.success) {
            alert('Área eliminada exitosamente');
            this.loadAreas();
          }
        },
        error: (error) => {
          console.error('Error al eliminar:', error);
          alert('Error al eliminar el área');
        }
      });
    }
  }

  viewExamenes(area: any): void {
    this.router.navigate(['configuraciones/examenes', area.id]);
  }

  
  changePage(page: number): void {
    this.currentPage = page;
    this.loadAreas();
  }

  changePageSize(newSize: number): void {
    this.pageSize = newSize;
    this.currentPage = 0;
    this.loadAreas();
  }
}