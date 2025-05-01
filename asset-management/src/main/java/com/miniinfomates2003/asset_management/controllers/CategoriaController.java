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

    @PutMapping("{idCategoria}")
    public ResponseEntity<CategoriaDTO> updateCategoria(@PathVariable(required = true) Integer idCategoria,
                                                        @RequestBody(required = true) CategoriaDTO categoriaDTO) {
        ResponseEntity responseEntity;

        // Comprobamos si existe una categoria cuyo id sea idCategoria
        if (!categoriaService.existsById(idCategoria)) {
            // No existe una categoria cuyo id sea idCategoria
            // Se debe devolver 404 con un mensaje de error
            String error = "La categoría con ID " + idCategoria + " no existe.";

            responseEntity = ResponseEntity.status(404).body(error);
        } else {
            // Existe una cuenta cuyo id sea idCategoria
            // Comprobamos si el usuario cuenta con los permisos de actualizacion
            if (!categoriaService.hasPermissionToUpdate(idCategoria)) {
                // El usuario no cuenta con los permisos de actualizacion
                // Se debe devolver 403 con un mensaje de error
                String error = "Sin permisos suficientes.";

                responseEntity = ResponseEntity.status(403).body(error).build();
            } else {
                // El usuario cuenta con los permisos de actualizacion
                responseEntity = ResponseEntity.ok().body(categoriaDTO);
            }
        }

        return responseEntity;
    }
}