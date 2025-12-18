import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { UserService } from '../../../core/services/user.service';
import Swal from 'sweetalert2';

import { HeaderComponent } from "../../../shared/header/header.component";
import { SidebarComponent } from "../../../shared/sidebar/sidebar.component";

@Component({
    selector: 'app-cambio-clave',
    standalone: true,
    imports: [CommonModule, ReactiveFormsModule, HeaderComponent, SidebarComponent],
    templateUrl: './cambio-clave.component.html'
})
export class CambioClaveComponent implements OnInit {
    changePasswordForm: FormGroup;
    loading = false;
    username = '';
    userId: number | null = null;

    constructor(
        private fb: FormBuilder,
        private userService: UserService,
        private router: Router
    ) {
        this.changePasswordForm = this.fb.group({
            oldPassword: ['', [Validators.required]],
            newPassword: ['', [Validators.required, Validators.minLength(6)]],
            confirmPassword: ['', [Validators.required]]
        }, {
            validators: this.passwordMatchValidator
        });
    }

    ngOnInit(): void {
        // Obtener datos del usuario logueado
        const currentUserStr = localStorage.getItem('currentUser');
        const userIdStr = localStorage.getItem('userId');

        if (currentUserStr) {
            const user = JSON.parse(currentUserStr);
            this.username = user.username || '';
        }

        if (userIdStr) {
            this.userId = parseInt(userIdStr, 10);
        }
    }

    passwordMatchValidator(g: FormGroup) {
        return g.get('newPassword')?.value === g.get('confirmPassword')?.value
            ? null : { mismatch: true };
    }

    onSubmit() {
        if (this.changePasswordForm.invalid) {
            return;
        }

        if (!this.userId) {
            Swal.fire('Error', 'No se pudo identificar al usuario', 'error');
            return;
        }

        this.loading = true;
        const { oldPassword, newPassword, confirmPassword } = this.changePasswordForm.value;

        this.userService.changePassword(this.userId, {
            oldPassword,
            newPassword,
            confirmPassword
        }).subscribe({
            next: () => {
                this.loading = false;
                Swal.fire({
                    title: '¡Éxito!',
                    text: 'Se cambió exitosamente...',
                    icon: 'success',
                    confirmButtonText: 'OK',
                    confirmButtonColor: '#2563EB'
                }).then(() => {
                    this.changePasswordForm.reset();
                });
            },
            error: (err) => {
                this.loading = false;
                let msg = 'Ocurrió un error al cambiar la contraseña';
                if (err.error && err.error.message) {
                    msg = err.error.message;
                }
                Swal.fire('Error', msg, 'error');
            }
        });
    }
}
