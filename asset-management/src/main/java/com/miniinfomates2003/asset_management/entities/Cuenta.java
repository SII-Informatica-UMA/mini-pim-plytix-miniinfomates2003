package main.java.com.miniinfomates2003.asset_management;

import java.sql.Date;
import java.util.Objects;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

public class Cuenta {
    private Integer id;
    private String nombre;
    private String direccion;
    private String nif;
    private Date fechaAlta;
    private Plan plan;

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

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getNif() {
        return nif;
    }

    public void setNif(String nif) {
        this.nif = nif;
    }

    public Date getFechaAlta() {
        return fechaAlta;
    }

    public void setFechaAlta(Date fechaAlta) {
        this.fechaAlta = fechaAlta;
    }

    public Plan getPlan() {
        return plan;
    }

    public void setPlan(Plan plan) {
        this.plan = plan;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, nombre, direccion, nif, fechaAlta, plan);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Cuenta cuenta = (Cuenta) obj;
        return Objects.equals(id, cuenta.id) &&
               Objects.equals(nombre, cuenta.nombre) &&
               Objects.equals(direccion, cuenta.direccion) &&
               Objects.equals(nif, cuenta.nif) &&
               Objects.equals(fechaAlta, cuenta.fechaAlta) &&
               Objects.equals(plan, cuenta.plan);
    }

    @Override
    public String toString() {
        return "Cuenta{" +
               "id=" + id +
               ", nombre='" + nombre + '\'' +
               ", direccion='" + direccion + '\'' +
               ", nif='" + nif + '\'' +
               ", fechaAlta=" + fechaAlta +
               ", plan=" + plan +
               '}';
    }
}
