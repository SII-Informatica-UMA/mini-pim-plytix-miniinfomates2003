package com.miniinfomates2003.asset_management.controllers;

import com.miniinfomates2003.asset_management.dtos.ActivoDTO;
import com.miniinfomates2003.asset_management.entities.Activo;
import com.miniinfomates2003.asset_management.exceptions.NoAccessException;
import com.miniinfomates2003.asset_management.services.ActivoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

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
        } catch (NoAccessException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error interno del servidor");
        }
    }
}
