package com.pe.laboratorio.config;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.pe.laboratorio.examtype.entity.ExamType;
import com.pe.laboratorio.examtype.repository.ExamTypeRepository;
import com.pe.laboratorio.permissions.entity.Permission;
import com.pe.laboratorio.permissions.repository.PermissionRepository;
import com.pe.laboratorio.roles.entity.Role;
import com.pe.laboratorio.roles.repository.RoleRepository;
import com.pe.laboratorio.users.entity.DatosPersonales;
import com.pe.laboratorio.users.repository.DatosPersonalesRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * DataInitializer - Carga datos iniciales según documento de requerimientos
 * Roles: Administrador, Médico, Tecnólogo Médico, Biólogo, Paciente
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

        private final PermissionRepository permissionRepository;
        private final RoleRepository roleRepository;
        private final DatosPersonalesRepository datosPersonalesRepository;
        private final PasswordEncoder passwordEncoder;
        private final ExamTypeRepository examTypeRepository;

        @Override
        public void run(String... args) {
                log.info("╔════════════════════════════════════════════════════════════════╗");
                log.info("║   📋 SISTEMA DE GESTIÓN DE LABORATORIO - DATA INITIALIZER    ║");
                log.info("╚════════════════════════════════════════════════════════════════╝");
                log.info("");

                if (permissionRepository.count() == 0) {
                        createPermissions();
                } else {
                        log.info("✓ Permissions already exist. Skipping creation.");
                        log.info("   ℹ️  Total permissions in database: {}", permissionRepository.count());
                }

                if (roleRepository.count() == 0) {
                        createRoles();
                } else {
                        log.info("✓ Roles already exist. Skipping creation.");
                        log.info("   ℹ️  Total roles in database: {}", roleRepository.count());
                }

                // Always check and ensure default users exist
                ensureDefaultUsers();

                // NUEVO: Crear tipos de examen
                if (examTypeRepository.count() == 0) {
                        createExamTypes();
                } else {
                        log.info("✓ Exam types already exist. Skipping creation.");
                        log.info("   ℹ️  Total exam types in database: {}", examTypeRepository.count());
                }

                log.info("");
                log.info("╔════════════════════════════════════════════════════════════════╗");
                log.info("║        ✅ DATA INITIALIZATION COMPLETED SUCCESSFULLY!         ║");
                log.info("╚════════════════════════════════════════════════════════════════╝");
                log.info("");
        }

        private void createPermissions() {
                log.info("┌────────────────────────────────────────────────────────────────┐");
                log.info("│  🔐 CREATING PERMISSIONS                                       │");
                log.info("└────────────────────────────────────────────────────────────────┘");
                log.info("");

                // ========== GESTIÓN DE USUARIOS ==========
                log.info("📁 Module: USER MANAGEMENT");
                List<Permission> userPermissions = Arrays.asList(
                                createPermission("USER_CREATE", "Crear usuarios del sistema", "user"),
                                createPermission("USER_READ", "Ver usuarios del sistema", "user"),
                                createPermission("USER_UPDATE", "Actualizar usuarios del sistema", "user"),
                                createPermission("USER_DELETE", "Eliminar usuarios del sistema", "user"),
                                createPermission("USER_BLOCK", "Bloquear/desbloquear cuentas de usuario", "user"));
                permissionRepository.saveAll(userPermissions);
                log.info("   ✓ Created {} user management permissions", userPermissions.size());
                log.info("");

                // ========== GESTIÓN DE ROLES Y PERMISOS ==========
                log.info("📁 Module: SECURITY (Roles & Permissions)");
                List<Permission> securityPermissions = Arrays.asList(
                                createPermission("ROLE_CREATE", "Crear roles", "security"),
                                createPermission("ROLE_READ", "Ver roles", "security"),
                                createPermission("ROLE_UPDATE", "Actualizar roles y asignar permisos", "security"),
                                createPermission("ROLE_DELETE", "Eliminar roles", "security"),
                                createPermission("PERMISSION_READ", "Ver permisos del sistema", "security"));
                permissionRepository.saveAll(securityPermissions);
                log.info("   ✓ Created {} security permissions", securityPermissions.size());
                log.info("");

                // ========== GESTIÓN DE CATÁLOGOS ==========
                log.info("📁 Module: CATALOGS");

                log.info("   📝 Exam Types...");
                List<Permission> examTypePermissions = Arrays.asList(
                                createPermission("EXAM_TYPE_CREATE", "Crear tipos de examen", "catalog"),
                                createPermission("EXAM_TYPE_READ", "Ver tipos de examen", "catalog"),
                                createPermission("EXAM_TYPE_UPDATE", "Actualizar tipos de examen", "catalog"),
                                createPermission("EXAM_TYPE_DELETE", "Eliminar tipos de examen", "catalog"));
                permissionRepository.saveAll(examTypePermissions);
                log.info("      ✓ Created {} exam type permissions", examTypePermissions.size());

                log.info("   📝 Exams...");
                List<Permission> examPermissions = Arrays.asList(
                                createPermission("EXAM_CREATE", "Crear exámenes/pruebas", "catalog"),
                                createPermission("EXAM_READ", "Ver exámenes/pruebas", "catalog"),
                                createPermission("EXAM_UPDATE", "Actualizar exámenes/pruebas", "catalog"),
                                createPermission("EXAM_DELETE", "Eliminar exámenes/pruebas", "catalog"));
                permissionRepository.saveAll(examPermissions);
                log.info("      ✓ Created {} exam permissions", examPermissions.size());

                log.info("   📝 Laboratory Areas...");
                List<Permission> labAreaPermissions = Arrays.asList(
                                createPermission("LAB_AREA_CREATE", "Crear áreas de laboratorio", "catalog"),
                                createPermission("LAB_AREA_READ", "Ver áreas de laboratorio", "catalog"),
                                createPermission("LAB_AREA_UPDATE", "Actualizar áreas de laboratorio", "catalog"),
                                createPermission("LAB_AREA_DELETE", "Eliminar áreas de laboratorio", "catalog"));
                permissionRepository.saveAll(labAreaPermissions);
                log.info("      ✓ Created {} lab area permissions", labAreaPermissions.size());

                log.info("   📝 Units...");
                List<Permission> unitPermissions = Arrays.asList(
                                createPermission("UNIT_CREATE", "Crear unidades de medida", "catalog"),
                                createPermission("UNIT_READ", "Ver unidades de medida", "catalog"),
                                createPermission("UNIT_UPDATE", "Actualizar unidades de medida", "catalog"),
                                createPermission("UNIT_DELETE", "Eliminar unidades de medida", "catalog"));
                permissionRepository.saveAll(unitPermissions);
                log.info("      ✓ Created {} unit permissions", unitPermissions.size());

                log.info("   📝 Profiles...");
                List<Permission> profilePermissions = Arrays.asList(
                                createPermission("PROFILE_CREATE", "Crear perfiles de pruebas", "catalog"),
                                createPermission("PROFILE_READ", "Ver perfiles de pruebas", "catalog"),
                                createPermission("PROFILE_UPDATE", "Actualizar perfiles de pruebas", "catalog"),
                                createPermission("PROFILE_DELETE", "Eliminar perfiles de pruebas", "catalog"));
                permissionRepository.saveAll(profilePermissions);
                log.info("      ✓ Created {} profile permissions", profilePermissions.size());
                log.info("");

                // ========== GESTIÓN DE PACIENTES ==========
                log.info("📁 Module: PATIENT MANAGEMENT");
                List<Permission> patientPermissions = Arrays.asList(
                                createPermission("PATIENT_CREATE", "Registrar pacientes", "patient"),
                                createPermission("PATIENT_READ", "Ver información de pacientes", "patient"),
                                createPermission("PATIENT_READ_ALL", "Ver todos los pacientes del sistema", "patient"),
                                createPermission("PATIENT_READ_OWN", "Ver solo pacientes propios atendidos", "patient"),
                                createPermission("PATIENT_READ_SELF", "Ver solo información propia (paciente)",
                                                "patient"),
                                createPermission("PATIENT_UPDATE", "Actualizar información de pacientes", "patient"),
                                createPermission("PATIENT_UPDATE_CONTACT",
                                                "Actualizar solo teléfono y email de paciente", "patient"),
                                createPermission("PATIENT_DELETE", "Eliminar pacientes", "patient"),
                                createPermission("PATIENT_HISTORY", "Ver historial completo del paciente", "patient"));
                permissionRepository.saveAll(patientPermissions);
                log.info("   ✓ Created {} patient management permissions", patientPermissions.size());
                log.info("");

                // ========== GESTIÓN DE ÓRDENES ==========
                log.info("📁 Module: ORDER MANAGEMENT");
                List<Permission> orderPermissions = Arrays.asList(
                                createPermission("ORDER_CREATE", "Crear órdenes de laboratorio", "order"),
                                createPermission("ORDER_READ", "Ver órdenes", "order"),
                                createPermission("ORDER_READ_ALL", "Ver todas las órdenes del sistema", "order"),
                                createPermission("ORDER_READ_OWN", "Ver solo órdenes de pacientes atendidos", "order"),
                                createPermission("ORDER_UPDATE", "Modificar datos de órdenes", "order"),
                                createPermission("ORDER_DELETE", "Eliminar órdenes", "order"),
                                createPermission("ORDER_ASSIGN", "Derivar y asignar exámenes", "order"),
                                createPermission("ORDER_CHANGE_STATUS",
                                                "Cambiar estado de órdenes (pendiente, en proceso, terminado)",
                                                "order"));
                permissionRepository.saveAll(orderPermissions);
                log.info("   ✓ Created {} order management permissions", orderPermissions.size());
                log.info("");

                // ========== GESTIÓN DE RESULTADOS ==========
                log.info("📁 Module: RESULT MANAGEMENT");
                List<Permission> resultPermissions = Arrays.asList(
                                createPermission("RESULT_CREATE", "Ingresar resultados de exámenes", "result"),
                                createPermission("RESULT_READ", "Ver resultados", "result"),
                                createPermission("RESULT_READ_ALL", "Ver todos los resultados del sistema", "result"),
                                createPermission("RESULT_READ_OWN", "Ver solo resultados de pacientes atendidos",
                                                "result"),
                                createPermission("RESULT_READ_SELF", "Ver solo resultados propios (paciente)",
                                                "result"),
                                createPermission("RESULT_UPDATE", "Editar resultados de exámenes", "result"),
                                createPermission("RESULT_DELETE", "Eliminar resultados", "result"),
                                createPermission("RESULT_VALIDATE_PRIMARY",
                                                "Realizar validación primaria de resultados (Tecnólogo)",
                                                "result"),
                                createPermission("RESULT_VALIDATE_FINAL",
                                                "Realizar validación final de resultados (Biólogo)",
                                                "result"),
                                createPermission("RESULT_DOWNLOAD", "Descargar resultados en PDF", "result"));
                permissionRepository.saveAll(resultPermissions);
                log.info("   ✓ Created {} result management permissions", resultPermissions.size());
                log.info("");

                // ========== REPORTES Y AUDITORÍA ==========
                log.info("📁 Module: REPORTS & AUDIT");
                List<Permission> reportPermissions = Arrays.asList(
                                createPermission("REPORT_GENERATE", "Generar reportes del sistema", "report"),
                                createPermission("REPORT_VIEW", "Ver reportes", "report"),
                                createPermission("REPORT_EXPORT", "Exportar reportes", "report"),
                                createPermission("AUDIT_VIEW", "Ver registros de auditoría", "audit"),
                                createPermission("AUDIT_EXPORT", "Exportar auditoría", "audit"));
                permissionRepository.saveAll(reportPermissions);
                log.info("   ✓ Created {} report & audit permissions", reportPermissions.size());
                log.info("");

                // ========== CONFIGURACIÓN DEL SISTEMA ==========
                log.info("📁 Module: SYSTEM CONFIGURATION");
                List<Permission> systemPermissions = Arrays.asList(
                                createPermission("SYSTEM_CONFIG", "Configurar parámetros del sistema", "system"),
                                createPermission("SYSTEM_BACKUP", "Realizar respaldos del sistema", "system"),
                                createPermission("SYSTEM_RESTORE", "Restaurar sistema desde respaldo", "system"));
                permissionRepository.saveAll(systemPermissions);
                log.info("   ✓ Created {} system configuration permissions", systemPermissions.size());
                log.info("");

                long totalPermissions = permissionRepository.count();
                log.info("╔════════════════════════════════════════════════════════════════╗");
                log.info("║  ✅ TOTAL PERMISSIONS CREATED: {:2d}                              ║", totalPermissions);
                log.info("╚════════════════════════════════════════════════════════════════╝");
                log.info("");
        }

        private Permission createPermission(String name, String description, String module) {
                return Permission.builder()
                                .name(name)
                                .description(description)
                                .module(module)
                                .active(true)
                                .build();
        }

        private void createRoles() {
                log.info("┌────────────────────────────────────────────────────────────────┐");
                log.info("│  👥 CREATING ROLES                                             │");
                log.info("└────────────────────────────────────────────────────────────────┘");
                log.info("");

                createAdministradorRole();
                createMedicoRole();
                createTecnologoRole();
                createBiologoRole();
                createPacienteRole();

                log.info("╔════════════════════════════════════════════════════════════════╗");
                log.info("║  ✅ ALL 5 ROLES CREATED SUCCESSFULLY                          ║");
                log.info("╚════════════════════════════════════════════════════════════════╝");
                log.info("");
        }

        private void createAdministradorRole() {
                log.info("🔧 Creating role: ADMINISTRADOR");
                log.info("   📋 Description: Acceso total al sistema (RF-ADM-01 a RF-ADM-04)");

                List<Permission> allPermissions = permissionRepository.findAll();

                Role administrador = Role.builder()
                                .name("ADMINISTRADOR")
                                .description("Acceso total al sistema - RF-ADM-01 a RF-ADM-04")
                                .active(true)
                                .permissions(new HashSet<>(allPermissions))
                                .build();

                roleRepository.save(administrador);
                log.info("   ✓ Assigned {} permissions (ALL PERMISSIONS)", allPermissions.size());
                log.info("   ✅ ADMINISTRADOR role created successfully");
                log.info("");
        }

        private void createMedicoRole() {
                log.info("🩺 Creating role: MEDICO");
                log.info("   📋 Description: Acceso consultivo a pacientes atendidos (RF-MED-01 a RF-MED-03)");

                Set<Permission> medicoPermissions = new HashSet<>();

                log.info("   📝 Assigning permissions:");
                medicoPermissions.add(findPermission("PATIENT_READ_OWN"));
                log.info("      ✓ PATIENT_READ_OWN");
                medicoPermissions.add(findPermission("PATIENT_HISTORY"));
                log.info("      ✓ PATIENT_HISTORY");
                medicoPermissions.add(findPermission("RESULT_READ_OWN"));
                log.info("      ✓ RESULT_READ_OWN");
                medicoPermissions.add(findPermission("RESULT_DOWNLOAD"));
                log.info("      ✓ RESULT_DOWNLOAD");
                medicoPermissions.add(findPermission("ORDER_READ_OWN"));
                log.info("      ✓ ORDER_READ_OWN");
                medicoPermissions.add(findPermission("EXAM_READ"));
                log.info("      ✓ EXAM_READ");
                medicoPermissions.add(findPermission("EXAM_TYPE_READ"));
                log.info("      ✓ EXAM_TYPE_READ");

                Role medico = Role.builder()
                                .name("MEDICO")
                                .description("Acceso consultivo restringido a pacientes atendidos - RF-MED-01 a RF-MED-03")
                                .active(true)
                                .permissions(medicoPermissions)
                                .build();

                roleRepository.save(medico);
                log.info("   ✓ Total permissions assigned: {} (READ-ONLY access)", medicoPermissions.size());
                log.info("   ✅ MEDICO role created successfully");
                log.info("");
        }

        private void createTecnologoRole() {
                log.info("🔬 Creating role: TECNOLOGO_MEDICO");
                log.info("   📋 Description: Operativo completo del laboratorio (RF-TEC-01 a RF-TEC-05)");

                Set<Permission> tecnologoPermissions = new HashSet<>();

                log.info("   📝 Assigning permissions:");
                log.info("      🏥 Patient Management:");
                tecnologoPermissions.add(findPermission("PATIENT_READ_ALL"));
                log.info("         ✓ PATIENT_READ_ALL");
                tecnologoPermissions.add(findPermission("PATIENT_UPDATE_CONTACT"));
                log.info("         ✓ PATIENT_UPDATE_CONTACT");
                tecnologoPermissions.add(findPermission("PATIENT_HISTORY"));
                log.info("         ✓ PATIENT_HISTORY");

                log.info("      📋 Order Management:");
                tecnologoPermissions.add(findPermission("ORDER_CREATE"));
                log.info("         ✓ ORDER_CREATE");
                tecnologoPermissions.add(findPermission("ORDER_READ_ALL"));
                log.info("         ✓ ORDER_READ_ALL");
                tecnologoPermissions.add(findPermission("ORDER_UPDATE"));
                log.info("         ✓ ORDER_UPDATE");
                tecnologoPermissions.add(findPermission("ORDER_ASSIGN"));
                log.info("         ✓ ORDER_ASSIGN");
                tecnologoPermissions.add(findPermission("ORDER_CHANGE_STATUS"));
                log.info("         ✓ ORDER_CHANGE_STATUS");

                log.info("      🧪 Result Management:");
                tecnologoPermissions.add(findPermission("RESULT_CREATE"));
                log.info("         ✓ RESULT_CREATE");
                tecnologoPermissions.add(findPermission("RESULT_READ_ALL"));
                log.info("         ✓ RESULT_READ_ALL");
                tecnologoPermissions.add(findPermission("RESULT_UPDATE"));
                log.info("         ✓ RESULT_UPDATE");
                tecnologoPermissions.add(findPermission("RESULT_VALIDATE_PRIMARY"));
                log.info("         ✓ RESULT_VALIDATE_PRIMARY");
                tecnologoPermissions.add(findPermission("RESULT_DOWNLOAD"));
                log.info("         ✓ RESULT_DOWNLOAD");

                log.info("      📚 Catalog Access:");
                tecnologoPermissions.add(findPermission("EXAM_READ"));
                tecnologoPermissions.add(findPermission("EXAM_TYPE_READ"));
                tecnologoPermissions.add(findPermission("LAB_AREA_READ"));
                tecnologoPermissions.add(findPermission("UNIT_READ"));
                tecnologoPermissions.add(findPermission("PROFILE_READ"));
                log.info("         ✓ READ access to all catalogs");

                log.info("      📊 Reports:");
                tecnologoPermissions.add(findPermission("REPORT_VIEW"));
                log.info("         ✓ REPORT_VIEW");

                Role tecnologo = Role.builder()
                                .name("TECNOLOGO_MEDICO")
                                .description("Operativo completo del laboratorio - RF-TEC-01 a RF-TEC-05")
                                .active(true)
                                .permissions(tecnologoPermissions)
                                .build();

                roleRepository.save(tecnologo);
                log.info("   ✓ Total permissions assigned: {}", tecnologoPermissions.size());
                log.info("   ✅ TECNOLOGO_MEDICO role created successfully");
                log.info("");
        }

        private void createBiologoRole() {
                log.info("🧬 Creating role: BIOLOGO");
                log.info("   📋 Description: Validación científica y revisión (RF-BIO-01 a RF-BIO-04)");

                Set<Permission> biologoPermissions = new HashSet<>();

                log.info("   📝 Assigning permissions:");
                log.info("      🏥 Patient Access:");
                biologoPermissions.add(findPermission("PATIENT_READ_ALL"));
                log.info("         ✓ PATIENT_READ_ALL");
                biologoPermissions.add(findPermission("PATIENT_HISTORY"));
                log.info("         ✓ PATIENT_HISTORY");

                log.info("      📋 Order Access:");
                biologoPermissions.add(findPermission("ORDER_READ_ALL"));
                log.info("         ✓ ORDER_READ_ALL");

                log.info("      🧪 Result Management & Validation:");
                biologoPermissions.add(findPermission("RESULT_READ_ALL"));
                log.info("         ✓ RESULT_READ_ALL");
                biologoPermissions.add(findPermission("RESULT_UPDATE"));
                log.info("         ✓ RESULT_UPDATE");
                biologoPermissions.add(findPermission("RESULT_VALIDATE_FINAL"));
                log.info("         ✓ RESULT_VALIDATE_FINAL (FINAL VALIDATION)");
                biologoPermissions.add(findPermission("RESULT_DOWNLOAD"));
                log.info("         ✓ RESULT_DOWNLOAD");
                biologoPermissions.add(findPermission("RESULT_CREATE"));
                log.info("         ✓ RESULT_CREATE (specialized exams)");

                log.info("      📚 Catalog Access:");
                biologoPermissions.add(findPermission("EXAM_READ"));
                biologoPermissions.add(findPermission("EXAM_TYPE_READ"));
                biologoPermissions.add(findPermission("LAB_AREA_READ"));
                biologoPermissions.add(findPermission("UNIT_READ"));
                biologoPermissions.add(findPermission("PROFILE_READ"));
                log.info("         ✓ READ access to all catalogs");

                log.info("      📊 Reports:");
                biologoPermissions.add(findPermission("REPORT_VIEW"));
                biologoPermissions.add(findPermission("REPORT_GENERATE"));
                log.info("         ✓ REPORT_VIEW & REPORT_GENERATE");

                Role biologo = Role.builder()
                                .name("BIOLOGO")
                                .description("Validación científica y revisión de resultados - RF-BIO-01 a RF-BIO-04")
                                .active(true)
                                .permissions(biologoPermissions)
                                .build();

                roleRepository.save(biologo);
                log.info("   ✓ Total permissions assigned: {}", biologoPermissions.size());
                log.info("   ✅ BIOLOGO role created successfully");
                log.info("");
        }

        private void createPacienteRole() {
                log.info("👤 Creating role: PACIENTE");
                log.info("   📋 Description: Acceso personal restringido (RF-PAC-01 a RF-PAC-04)");

                Set<Permission> pacientePermissions = new HashSet<>();

                log.info("   📝 Assigning permissions:");
                pacientePermissions.add(findPermission("PATIENT_READ_SELF"));
                log.info("      ✓ PATIENT_READ_SELF (own data only)");
                pacientePermissions.add(findPermission("RESULT_READ_SELF"));
                log.info("      ✓ RESULT_READ_SELF (own results only)");
                pacientePermissions.add(findPermission("RESULT_DOWNLOAD"));
                log.info("      ✓ RESULT_DOWNLOAD");

                Role paciente = Role.builder()
                                .name("PACIENTE")
                                .description("Acceso solo a resultados propios - RF-PAC-01 a RF-PAC-04")
                                .active(true)
                                .permissions(pacientePermissions)
                                .build();

                roleRepository.save(paciente);
                log.info("   ✓ Total permissions assigned: {} (RESTRICTED ACCESS)", pacientePermissions.size());
                log.info("   ✅ PACIENTE role created successfully");
                log.info("");
        }

        private Permission findPermission(String name) {
                return permissionRepository.findByName(name)
                                .orElseThrow(() -> new RuntimeException("Permission not found: " + name));
        }

        private void ensureDefaultUsers() {
                log.info("┌────────────────────────────────────────────────────────────────┐");
                log.info("│  👤 ENSURING DEFAULT USERS EXIST                               │");
                log.info("└────────────────────────────────────────────────────────────────┘");
                log.info("");

                // Usuario Administrador
                ensureUser("admin", "admin@laboratorio.com", "admin123", "Admin", "Sistema", "Admin", "ADMINISTRADOR");

                // Usuario Tecnólogo
                ensureUser("tecnologo", "tecnologo@laboratorio.com", "tec123", "Juan", "Pérez", "Garcia",
                                "TECNOLOGO_MEDICO");

                // Usuario Biólogo
                ensureUser("biologo", "biologo@laboratorio.com", "bio123", "María", "García", "Lopez", "BIOLOGO");

                log.info("");
        }

        private void ensureUser(String username, String email, String rawPassword, String nombre, String apepat,
                        String apemat,
                        String roleName) {
                log.info("Checking user: {}...", username);

                datosPersonalesRepository.findByLogin(username).ifPresentOrElse(
                                user -> {
                                        log.info("   ✓ User {} already exists. Updating data...", username);
                                        user.setPasswd(passwordEncoder.encode(rawPassword));
                                        user.setNombre(nombre);
                                        user.setApepat(apepat);
                                        user.setApemat(apemat);
                                        user.setActive(true);
                                        user.setSexo("M"); // Default to M

                                        // Garantizar que tenga el rol (si no lo tiene se agrega)
                                        Role role = roleRepository.findByName(roleName)
                                                        .orElseThrow(() -> new RuntimeException(
                                                                        "Role " + roleName + " not found"));
                                        user.getRoles().add(role);
                                        datosPersonalesRepository.save(user);
                                        log.info("   ✓ User updated: {}", username);
                                },
                                () -> {
                                        log.info("   Ops! User {} not found. Creating...", username);
                                        Role role = roleRepository.findByName(roleName)
                                                        .orElseThrow(() -> new RuntimeException(
                                                                        "Role " + roleName + " not found"));

                                        DatosPersonales newUser = DatosPersonales.builder()
                                                        .idPersonal(System.currentTimeMillis()) // Use millis for safer
                                                                                                // range
                                                        .login(username)
                                                        .email(email)
                                                        .passwd(passwordEncoder.encode(rawPassword))
                                                        .nombre(nombre)
                                                        .apepat(apepat)
                                                        .apemat(apemat)
                                                        .active(true)
                                                        .sexo("M") // Default
                                                        .roles(new HashSet<>(Arrays.asList(role)))
                                                        .build();

                                        datosPersonalesRepository.save(newUser);
                                        log.info("   ✅ Created user: {} / {}", username, rawPassword);
                                });
        }

        private void createExamTypes() {
                log.info("📊 Creating exam types...");

                List<ExamType> examTypes = Arrays.asList(
                                createExamType("CUANTITATIVO", "Resultados numéricos con valores de referencia"),
                                createExamType("CUALITATIVO",
                                                "Resultados descriptivos (Positivo/Negativo, Reactivo/No Reactivo)"),
                                createExamType("TEXTO", "Resultados interpretativos en texto libre"),
                                createExamType("IMAGEN", "Resultados en formato de imagen (PAP, Radiografías, etc.)"),
                                createExamType("PANEL", "Conjunto de exámenes agrupados (Perfiles)"));

                examTypeRepository.saveAll(examTypes);
                log.info("✅ Created {} exam types", examTypes.size());
        }

        private ExamType createExamType(String nombre, String descripcion) {
                return ExamType.builder()
                                .nombre(nombre)
                                .descripcion(descripcion)
                                .active(true)
                                .build();
        }
}