package com.miniinfomates2003.asset_management.entities;

import jakarta.persistence.*;

import java.util.Objects;
import java.util.Set;

@Entity
public class Categoria {
    @Id
    @GeneratedValue
    private Integer id;
    @Column(nullable = false)
    private String nombre;
    @Column(nullable = false)
    private Integer idCuenta;

    @ManyToMany
    @JoinTable(
            name = "activo_categoria",
            joinColumns = @JoinColumn(name = "categoria_id"),
            inverseJoinColumns = @JoinColumn(name = "activo_id")
    )
    private Set<Activo> activos;  // Relación con activos

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Set<Activo> getActivos() {
        return activos;
    }

    public void setActivos(Set<Activo> activos) {
        this.activos = activos;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, nombre);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Categoria categoria = (Categoria) obj;
        return Objects.equals(id, categoria.id) &&
               Objects.equals(nombre, categoria.nombre);
    }

    @Override
    public String toString() {
        return "Categoria{" +
               "id=" + id +
               ", nombre='" + nombre + '\'' +
               '}';
    }
}
