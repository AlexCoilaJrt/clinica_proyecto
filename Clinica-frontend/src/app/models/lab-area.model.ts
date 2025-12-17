export interface LabArea {
  id: number;
  codigo: string;
  descripcion: string;
  active: boolean;
  createdAt: string;
  updatedAt: string;
}

export interface LabAreaRequest {
  codigo: string;
  descripcion: string;
}

export interface PageResponse<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
}