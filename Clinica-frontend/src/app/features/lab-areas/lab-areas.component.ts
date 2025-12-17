import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { LabAreaService } from '../../services/lab-area.service';
import { LabArea, PageResponse } from '../../models/lab-area.model';
import { HeaderComponent } from "../../shared/header/header.component";
import { SidebarComponent } from "../../shared/sidebar/sidebar.component";

@Component({
  selector: 'app-lab-areas',
  standalone: true,
  imports: [CommonModule, FormsModule, HeaderComponent, SidebarComponent],
  templateUrl: './lab-areas.component.html',
  styleUrls: ['./lab-areas.component.css']
})
export class LabAreasComponent implements OnInit {
  areas: LabArea[] = [];
  currentPage: number = 0;
  pageSize: number = 10;
  totalPages: number = 0;
  totalElements: number = 0;

  // Modal state
  showModal: boolean = false;
  isEditMode: boolean = false;
  selectedArea: LabArea | null = null;

  // Form data
  formData = {
    codigo: '',
    descripcion: ''
  };

  public Math = Math;
  
  // Search
  searchQuery: string = '';

  constructor(
    private labAreaService: LabAreaService,
    private router: Router,
    private cdr: ChangeDetectorRef  // Inyectar ChangeDetectorRef
  ) { }

  ngOnInit(): void {
    // Usar setTimeout para evitar el error de detección de cambios
    setTimeout(() => {
      this.loadAreas();
    });
  }

  loadAreas(): void {
    if (this.searchQuery.trim()) {
      this.searchAreas();
    } else {
      this.labAreaService.getAll(this.currentPage, this.pageSize).subscribe({
        next: (response) => {
          if (response.success && response.data) {
            this.areas = response.data.content;
            this.totalPages = response.data.totalPages;
            this.totalElements = response.data.totalElements;
            this.currentPage = response.data.number;
            this.cdr.detectChanges(); // Forzar detección de cambios
            console.log('Áreas cargadas:', this.areas);
          }
        },
        error: (error) => {
          console.error('Error al cargar áreas:', error);
          alert('Error al cargar las áreas de laboratorio');
        }
      });
    }
  }

  searchAreas(): void {
    this.labAreaService.search(this.searchQuery, this.currentPage, this.pageSize).subscribe({
      next: (response) => {
        if (response.success && response.data) {
          this.areas = response.data.content;
          this.totalPages = response.data.totalPages;
          this.totalElements = response.data.totalElements;
          this.cdr.detectChanges();
        }
      },
      error: (error) => {
        console.error('Error en búsqueda:', error);
      }
    });
  }

  onSearch(): void {
    this.currentPage = 0;
    this.loadAreas();
  }

  openCreateModal(): void {
    this.isEditMode = false;
    this.selectedArea = null;
    this.formData = {
      codigo: '',
      descripcion: ''
    };
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
    this.formData = {
      codigo: '',
      descripcion: ''
    };
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
      // Actualizar
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
      // Crear
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

  viewExamenes(area: LabArea): void {
    this.router.navigate(['/examenes'], {
      queryParams: { areaId: area.id, areaCodigo: area.codigo }
    });
  }

  changePage(page: number): void {
    if (page >= 0 && page < this.totalPages) {
      this.currentPage = page;
      this.loadAreas();
    }
  }

  changePageSize(): void {
    this.currentPage = 0;
    this.loadAreas();
  }

  get pages(): number[] {
    return Array.from({ length: this.totalPages }, (_, i) => i);
  }
}