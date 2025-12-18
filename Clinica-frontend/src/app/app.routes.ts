import { Routes } from '@angular/router';
import { LoginComponent } from './features/auth/pages/login/login.component';
import { CambioSucursalComponent } from './features/auth/pages/cambio-sucursal/cambio-sucursal.component';
import { AuthGuard } from './core/guards/auth.guard';
import { USUARIOS_ROUTES } from './features/usuarios/users.routes';
<<<<<<< HEAD
import { REPORTES_ROUTES } from './features/reportes/reportes.routes';
=======
import { REPORTS_ROUTES } from './features/reports/reports.routes';
>>>>>>> ebdb651 (bitacora)

export const routes: Routes = [
    { path: '', component: LoginComponent },
    { path: 'cambio-sucursal', component: CambioSucursalComponent, canActivate: [AuthGuard] },
    {
        path: 'configuraciones',
        loadChildren: () => USUARIOS_ROUTES,
        canActivate: [AuthGuard]
    },
<<<<<<< HEAD

    {
        path: 'reportes',
        loadChildren: () => REPORTES_ROUTES,
        canActivate: [AuthGuard]
    },
]    
=======
    {
        path: 'reportes',
        loadChildren: () => REPORTS_ROUTES,
        canActivate: [AuthGuard]
    },

];
>>>>>>> ebdb651 (bitacora)
