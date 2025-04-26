package com.miniinfomates2003.asset_management.services;

import com.miniinfomates2003.asset_management.repositories.ActivoRepository;
import org.springframework.beans.factory.annotation.Autowired;

public class ActivoService {

    private final ActivoRepository activoRepository;
    private final CuentaService cuentaService;

    @Autowired
    public ActivoService(ActivoRepository activoRepository, CuentaService cuentaService) {
        this.activoRepository = activoRepository;
        this.cuentaService = cuentaService;
    }
}
