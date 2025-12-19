export enum TipoResultado {
  NUMERICO = 'NUMERICO',
  TEXTO = 'TEXTO',
  IMAGEN = 'IMAGEN',
  PANEL = 'PANEL',
  CUANTITATIVO = 'CUANTITATIVO',
  CUALITATIVO = 'CUALITATIVO'
}

export enum TipoMuestra {
  SANGRE = 'SANGRE',
  SUERO = 'SUERO',
  PLASMA = 'PLASMA',
  ORINA = 'ORINA',
  HECES = 'HECES',
  ESPUTO = 'ESPUTO',
  LIQUIDO_CEFALORRAQUIDEO = 'LIQUIDO_CEFALORRAQUIDEO',
  OTROS = 'OTROS'
}

export interface Exam {
  id: number;
  codigo: string;
  nombre: string;
  areaId: number;
  areaNombre: string;
  areaCodigo: string;
  tipoExamenId: number;
  tipoExamenNombre: string;
  metodo?: string;
  unidadMedida?: string;
  tipoMuestra?: TipoMuestra;
  tipoResultado: TipoResultado;
  precio?: number;
  valorMinimo?: number;
  valorMaximo?: number;
  valorCriticoMin?: number;
  valorCriticoMax?: number;
  tiempoEntrega?: number;
  indicaciones?: string;
  active: boolean;
  esPerfil: boolean;
  createdAt: string;
  updatedAt: string;
}

export interface ExamRequest {
  codigo: string;
  nombre: string;
  areaId: number;
  tipoExamenId: number;
  metodo?: string;
  unidadMedida?: string;
  tipoMuestra?: TipoMuestra;
  tipoResultado: TipoResultado;
  precio?: number;
  valorMinimo?: number;
  valorMaximo?: number;
  valorCriticoMin?: number;
  valorCriticoMax?: number;
  tiempoEntrega?: number;
  indicaciones?: string;
  esPerfil?: boolean;
}

export interface SubExam {
  id: number;
  examenId: number;
  examenNombre: string;
  examenCodigo: string;
  codigo: string;
  nombre: string;
  tipoResultado: TipoResultado;
  unidadMedida?: string;
  valorMinimo?: number;
  valorMaximo?: number;
  valorCriticoMin?: number;
  valorCriticoMax?: number;
  ordenVisualizacion: number;
  observaciones?: string;
  active: boolean;
  createdAt: string;
  updatedAt: string;
}

export interface SubExamRequest {
  examenId: number;
  codigo: string;
  nombre: string;
  tipoResultado: TipoResultado;
  unidadMedida?: string;
  valorMinimo?: number;
  valorMaximo?: number;
  valorCriticoMin?: number;
  valorCriticoMax?: number;
  ordenVisualizacion: number;
  observaciones?: string;
}

export interface ExamType {
  id: number;
  nombre: string;
  descripcion?: string;
  active: boolean;
}

export interface PageResponse<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
}