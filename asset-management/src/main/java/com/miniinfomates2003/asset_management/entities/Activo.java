package com.miniinfomates2003.asset_management.entities;

import java.util.Objects;
import java.util.Set;

import jakarta.persistence.*;

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
    @ElementCollection
    private Set<Integer> idProductos;

    @ManyToMany(mappedBy = "activos")
    private Set<Categoria> categorias;  // Relación bidireccional

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

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public Integer getTamanio() {
        return tamanio;
    }

    public void setTamanio(Integer tamanio) {
        this.tamanio = tamanio;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Set<Integer> getIdProductos() {
        return idProductos;
    }

    public void setIdProducto(Set<Integer> idProductos) {
        this.idProductos = idProductos;
    }

    public Set<Categoria> getCategorias() {
        return categorias;
    }

    public void setCategorias(Set<Categoria> categorias) {
        this.categorias = categorias;
    }

    // hashCode y equals (para comparar objetos correctamente)
    @Override
    public int hashCode() {
        return Objects.hash(id, nombre, tipo, tamanio, url, idProductos);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Activo activo = (Activo) obj;
        return Objects.equals(id, activo.id) &&
               Objects.equals(nombre, activo.nombre) &&
               Objects.equals(tipo, activo.tipo) &&
               Objects.equals(tamanio, activo.tamanio) &&
               Objects.equals(url, activo.url) &&
               Objects.equals(idProductos, activo.idProductos);
    }

    // toString (para representación en texto)
    @Override
    public String toString() {
        return "Activo{" +
               "id=" + id +
               ", nombre='" + nombre + '\'' +
               ", tipo='" + tipo + '\'' +
               ", tamanio=" + tamanio +
               ", url='" + url + '\'' +
               ", idProductos=" + idProductos +
               '}';
    }
}
