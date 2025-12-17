import { Routes } from '@angular/router';
import { PermisosComponent } from './permisos/permisos.component';
import { RolComponent } from './rol/rol.component';
import { GestionComponent } from './gestion/gestion.component';


export const USUARIOS_ROUTES: Routes = [
    { path: 'usuarios', component: GestionComponent },
    { path: 'ususarios', component: PermisosComponent },
    { path: 'roles', component: RolComponent },
];
