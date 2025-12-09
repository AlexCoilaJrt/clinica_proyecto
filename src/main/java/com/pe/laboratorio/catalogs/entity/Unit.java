package com.pe.laboratorio.catalogs.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "units")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Unit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    private String description;

    @Column(name = "is_enabled")
    private Boolean isEnabled = true;
}