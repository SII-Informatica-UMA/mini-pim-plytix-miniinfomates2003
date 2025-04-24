package com.miniinfomates2003.asset_management.controllers;
import com.miniinfomates2003.asset_management.dtos.CategoriaDTO;
import com.miniinfomates2003.asset_management.entities.Categoria;
import com.miniinfomates2003.asset_management.services.CategoriaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/categoria-activo")
public class CategoriaController {
    private final CategoriaService categoriaService;

    @Autowired
    public CategoriaController(CategoriaService categoriaService) {
        this.categoriaService = categoriaService;
    }

    @GetMapping
    public ResponseEntity<List<CategoriaDTO>> obtenerCategorias(@RequestParam(required = false) Integer idCuenta,
                                                @RequestParam(required = false) Integer idCategoria) {
        if (idCuenta != null) {
            return ResponseEntity.ok(categoriaService.obtenerPorCuenta(idCuenta).stream()
                    .map(Mapper::toDTO)
                    .collect(Collectors.toList()));
        } else if(idCategoria != null) {
            Optional<Categoria> categoriaOpt= categoriaService.obtenerPorCategoria(idCategoria);
            return categoriaOpt
                    .map(Mapper::toDTO)
                    .map((CategoriaDTO dto) -> {
                        List<CategoriaDTO> lista = List.of(dto);
                        return ResponseEntity.ok(lista);
                    })
                    .orElse(ResponseEntity.notFound().build());
        } else {
            return ResponseEntity.badRequest().build();
        }
    }
}
