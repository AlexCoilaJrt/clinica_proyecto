import { Routes } from '@angular/router';
import { PermisosComponent } from './permisos/permisos.component';
import { RolComponent } from './rol/rol.component';
import { GestionComponent } from './gestion/gestion.component';
import { LabAreasComponent } from '../lab-areas/lab-areas.component';
import { LabExamenesComponent } from '../lab-examenes/lab-examenes.component';
import { AuthGuard } from '../../core/guards/auth.guard';
import { SubExamenesComponent } from '../lab-examenes/sub-examenes/sub-examenes.component';


export const USUARIOS_ROUTES: Routes = [
    { path: 'usuarios', component: GestionComponent },
    { path: 'areas', component: LabAreasComponent },
<<<<<<< Updated upstream
    { path: 'roles', component: RolComponent },
    { path: 'cambio-clave', loadComponent: () => import('./cambio-clave/cambio-clave.component').then(m => m.CambioClaveComponent) },
=======
    { path: 'roles', component: RolComponent }, 
    { path: 'examenes', component: LabExamenesComponent },
    { path: 'examenes/:id', component: LabExamenesComponent },
    { path: 'sub-examenes', component: SubExamenesComponent },
    { path: 'sub-examenes/:id', component: SubExamenesComponent },
>>>>>>> Stashed changes
];
