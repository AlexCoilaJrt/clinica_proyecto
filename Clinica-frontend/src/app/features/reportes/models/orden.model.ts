export interface OrdenFilter {
  fechaInicio?: string;
  fechaFin?: string;
  patientId?: number;
  medicoId?: number;
  estado?: string;
  prioridad?: string;
}

export interface OrdenDetalle {
  id: number;
  examId: number;
  examName: string;
  equipoId?: number;
  estado: string;
  resultado?: string;
  valorReferencia?: string;
  unidad?: string;
  observaciones?: string;
  precio: number;
  valorCritico: boolean;
  fueraRango: boolean;
  validadoPrimario: boolean;
  validadoFinal: boolean;
  tecnologoName?: string;
  biologoName?: string;
  procesadoPorName?: string;
  validadoPorName?: string;
  fechaProcesamiento?: string;
  fechaValidacionPrimaria?: string;
  fechaValidacionFinal?: string;
  createdAt: string;
  updatedAt: string;
}

export interface Orden {
  id: number;
  numeroOrden: string;
  fechaOrden: string;
  diagnostico?: string;
  estado: string;
  prioridad: string;
  tipoAtencion?: string;
  tipoMuestra?: string;
  observaciones?: string;
  total: number;
  fechaTomaMuestra?: string;
  fechaProcesamiento?: string;
  fechaValidacion?: string;
  fechaEntrega?: string;
  patientId: number;
  patientFirstName: string;
  patientLastName: string;
  patientDni: string;
  medicoId?: number;
  medicoNombre?: string;
  medicoFullName?: string;
  userId?: number;
  userName?: string;
  validadoPorId?: number;
  validadoPorName?: string;
  detalles: OrdenDetalle[];
  createdAt: string;
  updatedAt: string;
  createdByName?: string;
}
