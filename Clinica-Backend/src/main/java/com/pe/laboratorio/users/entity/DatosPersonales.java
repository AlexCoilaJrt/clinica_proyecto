package com.pe.laboratorio.users.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.pe.laboratorio.permissions.entity.Permission;
import com.pe.laboratorio.roles.entity.Role;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "datos_personales")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DatosPersonales implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "id_personal")
    private Long idPersonal;

    @Column(length = 40)
    private String nombre;

    @Column(name = "apemat", length = 300)
    private String apemat;

    @Column(name = "apepat", length = 300)
    private String apepat;

    @Column(name = "ver_nombre", length = 300)
    private String verNombre;

    @Column(name = "ver_apepat", length = 200)
    private String verApepat;

    @Column(name = "ver_apemat", length = 400)
    private String verApemat;

    @Column(length = 3)
    private String sexo;

    @Column(name = "nacfec")
    private LocalDate nacfec;

    @Column(name = "naclug", length = 80)
    private String naclug;

    @Column(name = "naclocl", length = 70)
    private String naclocl;

    @Column(name = "estciv", length = 1)
    private String estciv;

    @Column(length = 12)
    private String rhc;

    @Column(name = "tipodoc", length = 2)
    private String tipodoc;

    @Column(name = "numdoc", length = 15)
    private String numdoc;

    @Column(length = 40)
    private String direcc;

    @Column(name = "domref", length = 120)
    private String domref;

    @Column(name = "domloc", length = 120)
    private String domloc;

    @Column(name = "fon_local", length = 25)
    private String fonLocal;

    @Column(name = "fono2", length = 25)
    private String fono2;

    @Column(name = "login", length = 30)
    private String login;

    @Column(name = "passwd", length = 250)
    private String passwd;

    @Column(name = "id_inaru", length = 20)
    private String idInaru;

    @Column(name = "id_ocupac", length = 25)
    private String idOcupac;

    @Column(length = 40)
    private String email;

    @Column(length = 60)
    private String foto;

    @Column(name = "ruc", length = 12)
    private String ruc;

    @Column(name = "grado_inaru", length = 1)
    private String gradoInaru;

    @Column(name = "tipo_sangre", length = 30)
    private String tipoSangre;

    @Column(name = "cem_labor", length = 200)
    private String cemLabor;

    @Column(name = "direc_labor", length = 200)
    private String direcLabor;

    @Column(name = "fono_labor", length = 10)
    private String fonoLabor;

    @Column(name = "anexo_labor", length = 6)
    private String anexoLabor;

    @Column(name = "id_prosed")
    private Integer idProsed;

    @Column(name = "nombre_prosed", length = 40)
    private String nombreProsed;

    @Column(name = "fecha_ing_rna")
    private LocalDate fechaIngRna;

    @Column(name = "observacion", length = 500)
    private String observacion;

    @Column(name = "foto_camino_ficha", length = 100)
    private String fotoCaminoFicha;

    @Column(name = "fallecido")
    private Boolean fallecido;

    @Column(name = "fecha_fallec")
    private LocalDate fechaFallec;

    @Column(name = "id_ultimo_cue")
    private Integer idUltimoCue;

    @Column(name = "id_personal_user", length = 20)
    private String idPersonalUser;

    @Column(name = "ultimo_naru", length = 20)
    private String ultimoNaru;

    @Column(name = "ultima_razon", length = 150)
    private String ultimaRazon;

    @Column(name = "id_juridico", length = 20)
    private String idJuridico;

    @Column(name = "nom_ocupacion", length = 200)
    private String nomOcupacion;

    @Column(name = "num_folio_integer")
    private Integer numFolioInteger;

    @Column(name = "arch_lista", length = 100)
    private String archLista;

    @Column(name = "ultima_direc_cue", length = 200)
    private String ultimaDirecCue;

    @Column(name = "grupos_sanguineo", length = 20)
    private String gruposSanguineo;

    @Column(length = 20)
    private String ubicacion;

    @Column(name = "ma_miembr", length = 400)
    private String maMiembr;

    @Column(name = "ma_ape_par", length = 40)
    private String maApePar;

    @Column(name = "ma_ape_mat", length = 40)
    private String maApeMat;

    @Column(name = "pa_nombre", length = 40)
    private String paNombre;

    @Column(name = "pa_ape_pat", length = 40)
    private String paApePat;

    @Column(name = "pa_ape_mat", length = 40)
    private String paApeMat;

    @Column(length = 40)
    private String religion;

    @Column(name = "grado_inaru1", length = 4000)
    private String gradoInaru1;

    @Column(name = "grado_inaru_l", length = 4000)
    private String gradoInaruL;

    @Column(length = 4000)
    private String profesion;

    @Column(length = 500)
    private String ocupacion;

    @Column(name = "id_religion", length = 20)
    private String idReligion;

    @Column(name = "raza_humana", length = 100)
    private String razaHumana;

    @Column(name = "lugar_procedencia", length = 200)
    private String lugarProcedencia;

    @Column(name = "grado_instruccion", length = 200)
    private String gradoInstruccion;

    @Column(name = "dni_ref", length = 25)
    private String dniRef;

    @Column(name = "rhc_old", length = 25)
    private String rhcOld;

    @Column(name = "fecha_baja")
    private LocalDate fechaBaja;

    // Campos de auditoría y control
    @Column(nullable = false)
    @Builder.Default
    private Boolean active = true;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "last_login")
    private LocalDateTime lastLogin;

    // Relación Many-to-Many con Role
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "role_id"))
    @Builder.Default
    private Set<Role> roles = new HashSet<>();

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // ========================================
    // UserDetails Methods (Spring Security)
    // ========================================

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Set<GrantedAuthority> authorities = new HashSet<>();

        // Agregar roles como ROLE_NOMBRE
        roles.forEach(role -> {
            authorities.add(new SimpleGrantedAuthority("ROLE_" + role.getName()));

            // Agregar cada permiso del rol
            role.getPermissions()
                    .forEach(permission -> authorities.add(new SimpleGrantedAuthority(permission.getName())));
        });

        return authorities;
    }

    @Override
    public String getPassword() {
        return passwd;
    }

    @Override
    public String getUsername() {
        return login;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return active;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return active;
    }

    // ========================================
    // Helper Methods
    // ========================================

    /**
     * Obtiene todos los nombres de permisos del usuario
     */
    public Set<String> getPermissionNames() {
        return roles.stream()
                .flatMap(role -> role.getPermissions().stream())
                .map(Permission::getName)
                .collect(java.util.stream.Collectors.toSet());
    }

    /**
     * Obtiene todos los nombres de roles del usuario
     */
    public Set<String> getRoleNames() {
        return roles.stream()
                .map(Role::getName)
                .collect(java.util.stream.Collectors.toSet());
    }

    /**
     * Verifica si el usuario tiene un permiso específico
     */
    public boolean hasPermission(String permissionName) {
        return getPermissionNames().contains(permissionName);
    }

    /**
     * Verifica si el usuario tiene un rol específico
     */
    public boolean hasRole(String roleName) {
        return roles.stream()
                .anyMatch(role -> role.getName().equalsIgnoreCase(roleName));
    }

    /**
     * Obtiene el nombre completo del usuario
     */
    public String getFullName() {
        if (nombre != null && apepat != null && apemat != null) {
            return nombre + " " + apepat + " " + apemat;
        }
        if (nombre != null && apepat != null) {
            return nombre + " " + apepat;
        }
        return login != null ? login : String.valueOf(idPersonal);
    }

    /**
     * Obtiene el nombre completo verificado
     */
    public String getVerifiedFullName() {
        if (verNombre != null && verApepat != null && verApemat != null) {
            return verNombre + " " + verApepat + " " + verApemat;
        }
        return getFullName();
    }
}