import { Routes } from '@angular/router';
import { LoginComponent } from './features/auth/pages/login/login.component';
import { PortalClinicoLoginComponent } from './features/auth/pages/portal-clinico-login/portal-clinico-login.component';
import { CambioSucursalComponent } from './features/auth/pages/cambio-sucursal/cambio-sucursal.component';
import { AuthGuard } from './core/guards/auth.guard';
import { USUARIOS_ROUTES } from './features/usuarios/users.routes';

export const routes: Routes = [
    { path: 'login', component: LoginComponent },
    { path: 'portal-clinico', component: PortalClinicoLoginComponent },
    { path: 'cambio-sucursal', component: CambioSucursalComponent, canActivate: [AuthGuard] },
    {
        path: 'usuarios',
        loadChildren: () => USUARIOS_ROUTES
    },
];
