package com.miniinfomates2003.asset_management.controllers;

import com.miniinfomates2003.asset_management.dtos.ActivoDTO;
import com.miniinfomates2003.asset_management.entities.Activo;
import com.miniinfomates2003.asset_management.exceptions.NoAccessException;
import com.miniinfomates2003.asset_management.exceptions.NotFoundException;
import com.miniinfomates2003.asset_management.exceptions.TokenMissingException;
import com.miniinfomates2003.asset_management.services.ActivoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/activo")
public class ActivoController {

    private final ActivoService activoService;

    @Autowired
    public ActivoController(ActivoService activoService) {
        this.activoService = activoService;
    }

    @PostMapping
    public ResponseEntity<?> crearActivo(@RequestBody ActivoDTO activoDTO,
                                         @RequestParam Integer idCuenta,
                                         UriComponentsBuilder builder) {
        try {
            Activo activo = Mapper.toEntity(activoDTO);
            activo.setIdCuenta(idCuenta);
            activo = activoService.aniadirActivo(activo, idCuenta);
            URI uri = builder
                    .path("/activo/{id}")
                    .buildAndExpand(activo.getId())
                    .toUri();
            return ResponseEntity.created(uri).body(Mapper.toDTO(activo));
        } catch (NotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (NoAccessException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<?> obtenerActivos(@RequestParam(required = false) Integer idActivo,
                                            @RequestParam(required = false) Integer idCategoria,
                                            @RequestParam(required = false) Integer idProducto,
                                            @RequestParam(required = false) Integer idCuenta) {
        try {
            if (idActivo != null) {
                Optional<Activo> activoOpt = activoService.obtenerPorActivo(idActivo);
                ActivoDTO dto = activoOpt
                        .map(a -> Mapper.toDTO(a))
                        .orElseThrow(NoAccessException::new);

                return ResponseEntity.ok(List.of(dto));
            } else if (idCategoria != null) {
                var activos = activoService.obtenerPorCategoria(idCategoria)
                        .stream()
                        .map(Mapper::toDTO)
                        .collect(Collectors.toList());
                return ResponseEntity.ok(activos);
            } else if (idProducto != null) {
                var activos = activoService.obtenerPorProducto(idProducto)
                        .stream()
                        .map(Mapper::toDTO)
                        .collect(Collectors.toList());
                return ResponseEntity.ok(activos);
            } else if (idCuenta != null) {
                var activos = activoService.obtenerPorCuenta(idCuenta)
                        .stream()
                        .map(Mapper::toDTO)
                        .collect(Collectors.toList());
                return ResponseEntity.ok(activos);
            } else  {
                return ResponseEntity.badRequest().build();
            }
        } catch (NoAccessException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (NotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @PutMapping("/{idActivo}")
    public ResponseEntity<ActivoDTO> updateActivo(@PathVariable(required = true) Integer idActivo,
                                                  @RequestBody(required = true) ActivoDTO activoDTO) {
        try {
            return ResponseEntity.ok(Mapper.toDTO(activoService.updateActivo(idActivo, activoDTO)));
        } catch (NotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (NoAccessException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/{idActivo}")
    public ResponseEntity<Void> deleteActivo(@PathVariable(required = true) Integer idActivo) {
        try {
            activoService.deleteActivo(idActivo);
            return ResponseEntity.ok().build();
        } catch (NotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (NoAccessException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
