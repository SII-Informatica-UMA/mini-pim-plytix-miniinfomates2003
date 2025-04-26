package com.miniinfomates2003.asset_management.controllers;

import com.miniinfomates2003.asset_management.services.ActivoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/activo")
public class ActivoController {
    private final ActivoService activoService;

    @Autowired
    public ActivoController(ActivoService activoService) {
        this.activoService = activoService;
    }

    @PutMapping
    public void updateActivo() {

    }
}
