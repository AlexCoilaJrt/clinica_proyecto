import { Routes } from '@angular/router';
import { BitacoraComponent } from './pages/bitacora/bitacora.component';

export const REPORTS_ROUTES: Routes = [
    {
        path: 'bitacora',
        component: BitacoraComponent,
        runGuardsAndResolvers: 'always'
    },
    // Aquí se agregarían las otras rutas de reportes (estadisticas, atenciones, etc.)
];
