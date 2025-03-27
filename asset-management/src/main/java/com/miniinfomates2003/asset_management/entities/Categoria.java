package main.java.com.miniinfomates2003.asset_management.entities;

import jakarta.persistence.*;

@Entity
public class Categoria {
    @Id
    @GeneratedValue
    private Integer id;
    private String nombre;

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
