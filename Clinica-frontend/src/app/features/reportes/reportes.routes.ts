import { Routes } from '@angular/router';
import { AtencionesComponent } from './atenciones/atenciones.component';

export const REPORTES_ROUTES: Routes = [
    { path: 'atenciones', component: AtencionesComponent },
    // Aquí puedes agregar más rutas de reportes en el futuro
    // { path: 'estadisticas', component: EstadisticasComponent },
    // { path: 'bitacora', component: BitacoraComponent },
];
