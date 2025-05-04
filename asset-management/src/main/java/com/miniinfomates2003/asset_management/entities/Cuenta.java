package com.miniinfomates2003.asset_management.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Cuenta {

    private Integer id;
    private String nombre;
    private String direccion;
    private String nif;
    private Date fechaAlta;
    private Plan plan;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Plan {
        private Integer id;
        private String nombre;
        private Integer maxProductos;
        private Integer maxActivos;
        private Integer maxAlmacenamiento;
        private Integer maxCategoriasProductos;
        private Integer maxCategoriasActivos;
        private Integer maxRelaciones;
        private Double precio;
    }
}
