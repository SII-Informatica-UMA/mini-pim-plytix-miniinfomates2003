package com.miniinfomates2003.asset_management.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.Set;

@Entity
@Data
@EqualsAndHashCode
@ToString(exclude = "activos")
public class Categoria {
    @Id
    @GeneratedValue
    private Integer id;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false)
    @EqualsAndHashCode.Exclude
    private Integer idCuenta;

    @ManyToMany
    @JoinTable(
            name = "activo_categoria",
            joinColumns = @JoinColumn(name = "categoria_id"),
            inverseJoinColumns = @JoinColumn(name = "activo_id")
    )
    @EqualsAndHashCode.Exclude
    private Set<Activo> activos;  // Relación con activos
}