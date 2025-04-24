package com.miniinfomates2003.asset_management.entities;

import java.util.Set;

import jakarta.persistence.*;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString(exclude = "categorias")
@Builder
@Entity
public class Activo {
    @Id
    @GeneratedValue
    private Integer id;

    @Column(nullable = false)
    private String nombre;

    private String tipo;

    @Column(nullable = false)
    private Integer tamanio;

    private String url;

    @ManyToMany(mappedBy = "activos")
    @EqualsAndHashCode.Exclude
    private Set<Categoria> categorias;  // Relación bidireccional

    @ElementCollection
    @EqualsAndHashCode.Exclude
    private Set<Integer> idProductos;

    @Column(nullable = false)
    @EqualsAndHashCode.Exclude
    private Integer idCuenta;
}