package com.miniinfomates2003.asset_management.dtos;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ActivoDTO {
    private Integer id;
    private String nombre;
    private String tipo;
    private Integer tamanio;
    private String url;
    private List<CategoriaDTO> categorias;
    private List<Integer> productos;
}
