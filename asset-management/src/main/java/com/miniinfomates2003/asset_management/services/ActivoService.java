package com.miniinfomates2003.asset_management.services;

import com.miniinfomates2003.asset_management.entities.Activo;
import com.miniinfomates2003.asset_management.exceptions.TokenMissingException;
import com.miniinfomates2003.asset_management.repositories.ActivoRepository;
import com.miniinfomates2003.asset_management.security.SecurityConfguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ActivoService {

//    private final ActivoRepository activoRepository;
//    private final CuentaService cuentaService;
//
//    @Autowired
//    public ActivoService(ActivoRepository activoRepository, CuentaService cuentaService) {
//        this.activoRepository = activoRepository;
//        this.cuentaService = cuentaService;
//    }
//
//    public Activo aniadirActivo(Activo activo) {
//        var usuario = SecurityConfguration.getAuthenticatedUser()
//                .orElseThrow(TokenMissingException::new);
//    }
}
