package com.miniinfomates2003.asset_management.controllers;

import com.miniinfomates2003.asset_management.services.ActivoService;
import org.springframework.beans.factory.annotation.Autowired;

public class ActivoController {
    private final ActivoService activoService;

    @Autowired
    public ActivoController(ActivoService activoService) {
        this.activoService = activoService;
    }
}
