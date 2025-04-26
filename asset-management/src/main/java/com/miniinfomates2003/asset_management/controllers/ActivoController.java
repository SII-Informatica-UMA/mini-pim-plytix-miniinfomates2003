package com.miniinfomates2003.asset_management.controllers;

import com.miniinfomates2003.asset_management.dtos.ActivoDTO;
import com.miniinfomates2003.asset_management.entities.Activo;
import com.miniinfomates2003.asset_management.services.ActivoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/activo")
public class ActivoController {

//    private final ActivoService activoService;
//
//    @Autowired
//    public ActivoController(ActivoService activoService) {
//        this.activoService = activoService;
//    }
//
//    @PutMapping
//    public ResponseEntity<?> crearActivo(@RequestBody ActivoDTO activoDTO,
//                                         UriComponentsBuilder builder) {
//        try {
//            Activo activo = Mapper.toEntity(activoDTO);
//            activo = activoService.aniadirActivo(activo);
//            URI uri = builder
//                    .path("/activo/{id}")
//                    .buildAndExpand(activo.getId())
//                    .toUri();
//            return ResponseEntity.created(uri).body(Mapper.toDTO(activo));
//        }
//    }
}
