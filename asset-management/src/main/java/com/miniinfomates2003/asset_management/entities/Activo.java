package com.miniinfomates2003.asset_management.entities;

import java.util.Set;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Entity
@Data
@EqualsAndHashCode(of = {"id", "nombre", "tipo", "tamanio", "url", "idProductos"})
@ToString(exclude = "categorias")
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

    @ElementCollection
    @EqualsAndHashCode.Exclude
    private Set<Integer> idProductos;

    @Column(nullable = false)
    @EqualsAndHashCode.Exclude
    private Integer idCuenta;

    @ManyToMany(mappedBy = "activos")
    @EqualsAndHashCode.Exclude
    private Set<Categoria> categorias;  // Relación bidireccional
}