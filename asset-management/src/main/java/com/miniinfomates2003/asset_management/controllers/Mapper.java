package com.miniinfomates2003.asset_management.controllers;
import com.miniinfomates2003.asset_management.entities.Activo;
import com.miniinfomates2003.asset_management.dtos.ActivoDTO;
import com.miniinfomates2003.asset_management.entities.Categoria;
import com.miniinfomates2003.asset_management.dtos.CategoriaDTO;

import java.util.*;
import java.util.stream.Collectors;

public class Mapper {

    public static Categoria toEntity(CategoriaDTO dto) {
        if (dto == null) {
            return null;
        }
        return Categoria.builder()
                .id(dto.getId())
                .nombre(dto.getNombre())
                .build();
    }

    public static CategoriaDTO toDTO(Categoria categoria) {
        if (categoria == null) {
            return null;
        }
        return CategoriaDTO.builder()
                .id(categoria.getId())
                .nombre(categoria.getNombre())
                .build();
    }

    public static Activo toEntity(ActivoDTO dto) {
        if (dto == null) {
            return null;
        }

        // Preparamos las categorías con builder incluyendo solo el id de cada una
        Set<Categoria> categorias = new HashSet<>();
        if (dto.getCategorias() != null) {
            categorias = dto.getCategorias().stream()
                    .map(categoriaDTO -> Categoria.builder()
                            .id(categoriaDTO.getId())
                            .build())
                    .collect(Collectors.toSet());
        }

        // Preparamos los productos
        Set<Integer> productos = new HashSet<>();
        if (dto.getProductos() != null) {
            productos = new HashSet<>(dto.getProductos());
        }

        // Usamos el patrón Builder para crear la entidad Activo
        return Activo.builder()
                .id(dto.getId())
                .nombre(dto.getNombre())
                .tipo(dto.getTipo())
                .tamanio(dto.getTamanio())
                .url(dto.getUrl())
                .idProductos(productos)
                .categorias(categorias)
                .build();
    }

    public static ActivoDTO toDTO(Activo activo) {
        if (activo == null) {
            return null;
        }

        // Convertimos Set<Categoria> a List<CategoriaDTO>
        List<CategoriaDTO> categoriasDTO = new ArrayList<>();
        if (activo.getCategorias() != null && !activo.getCategorias().isEmpty()) {
            categoriasDTO = activo.getCategorias().stream()
                    .map(categoria -> CategoriaDTO.builder()
                            .id(categoria.getId())
                            .nombre(categoria.getNombre())
                            .build())
                    .toList();
        }

        // Convertimos Set<Integer> a List<Integer>
        List<Integer> productos = new ArrayList<>();
        if (activo.getIdProductos() != null && !activo.getIdProductos().isEmpty()) {
            productos = new ArrayList<>(activo.getIdProductos());
        }

        // Construir el DTO usando el patrón Builder
        return ActivoDTO.builder()
                .id(activo.getId())
                .nombre(activo.getNombre())
                .tipo(activo.getTipo())
                .tamanio(activo.getTamanio())
                .url(activo.getUrl())
                .categorias(categoriasDTO)
                .productos(productos)
                .build();
    }
}