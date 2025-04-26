package com.miniinfomates2003.asset_management.controllers;

import com.miniinfomates2003.asset_management.dtos.CategoriaDTO;
import com.miniinfomates2003.asset_management.exceptions.NotFoundException;
import com.miniinfomates2003.asset_management.services.CategoriaService;
import com.miniinfomates2003.asset_management.exceptions.TokenMissingException;
import com.miniinfomates2003.asset_management.exceptions.NoAccessException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
        } catch (TokenMissingException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        } catch (NoAccessException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (NotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error interno del servidor");
        }
    }
}