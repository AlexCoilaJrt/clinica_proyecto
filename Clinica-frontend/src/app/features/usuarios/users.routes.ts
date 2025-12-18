import { Routes } from '@angular/router';
import { PermisosComponent } from './permisos/permisos.component';
import { RolComponent } from './rol/rol.component';
import { GestionComponent } from './gestion/gestion.component';
import { LabAreasComponent } from '../lab-areas/lab-areas.component';


export const USUARIOS_ROUTES: Routes = [
    { path: 'usuarios', component: GestionComponent },
    { path: 'areas', component: LabAreasComponent },
    { path: 'roles', component: RolComponent },
    { path: 'cambio-clave', loadComponent: () => import('./cambio-clave/cambio-clave.component').then(m => m.CambioClaveComponent) },
];
