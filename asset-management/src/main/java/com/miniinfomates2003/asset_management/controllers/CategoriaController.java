package com.miniinfomates2003.asset_management.controllers;

import com.miniinfomates2003.asset_management.dtos.CategoriaDTO;
import com.miniinfomates2003.asset_management.entities.Categoria;
import com.miniinfomates2003.asset_management.exceptions.NotFoundException;
import com.miniinfomates2003.asset_management.services.CategoriaService;
import com.miniinfomates2003.asset_management.exceptions.TokenMissingException;
import com.miniinfomates2003.asset_management.exceptions.NoAccessException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/categoria-activo")
public class CategoriaController {

    private final CategoriaService categoriaService;

    @Autowired
    public CategoriaController(CategoriaService categoriaService) {
        this.categoriaService = categoriaService;
    }

    @PostMapping
    public ResponseEntity<?> crearCategoria(@RequestBody CategoriaDTO categoriaDTO,
                                            @RequestParam Integer idCuenta,
                                            UriComponentsBuilder builder) {
        try {
            Categoria categoria = Mapper.toEntity(categoriaDTO);
            categoria.setIdCuenta(idCuenta);
            categoria = categoriaService.aniadirCategoria(categoria, idCuenta);
            URI uri = builder
                    .path("/categoria-activo/{id}")
                    .buildAndExpand(categoria.getId())
                    .toUri();
            return ResponseEntity.created(uri).body(Mapper.toDTO(categoria));
        } catch (NotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (NoAccessException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<?> obtenerCategorias(@RequestParam(required = false) Integer idCuenta,
                                               @RequestParam(required = false) Integer idCategoria) {
        try {
            if (idCuenta != null) {
                var categorias = categoriaService.obtenerPorCuenta(idCuenta)
                        .stream()
                        .map(Mapper::toDTO)
                        .collect(Collectors.toList());
                return ResponseEntity.ok(categorias);
            } else if (idCategoria != null) {
                var categoria = categoriaService.obtenerPorCategoria(idCategoria)
                        .map(Mapper::toDTO)
                        .map(List::of)
                        .orElseThrow(NoAccessException::new);
                return ResponseEntity.ok(categoria);
            } else {
                return ResponseEntity.badRequest().build();
            }
        } catch (NoAccessException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (NotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("{idCategoria}")
    public ResponseEntity<CategoriaDTO> updateCategoria(@PathVariable(required = true) Integer idCategoria,
                                                        @RequestBody(required = true) CategoriaDTO categoriaDTO) {
        try {
            // Extraemos la entidad de CategoriaDTO
            Categoria categoria = Mapper.toEntity(categoriaDTO);

            // Actualizamos si id para que coincida con el de la categoria existente
            categoria.setId(idCategoria);

            // Guardamos la categoria ya actualizada
            CategoriaDTO categoriaActualizada = Mapper.toDTO(categoriaService.updateCategoria(idCategoria, categoria));

            return ResponseEntity.ok().body(categoriaActualizada);
        } catch (NotFoundException nfe) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (NoAccessException nae) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("{idCategoria}")
    public ResponseEntity<CategoriaDTO> deleteCategoria(@PathVariable(required = true) Integer idCategoria) {
        try {
            categoriaService.deleteCategoria(idCategoria);

            return ResponseEntity.ok().build();
        } catch (NotFoundException nfe) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (NoAccessException nae) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }
}
