package com.miniinfomates2003.asset_management.services;

import com.miniinfomates2003.asset_management.entities.Activo;
import com.miniinfomates2003.asset_management.exceptions.NoAccessException;
import com.miniinfomates2003.asset_management.exceptions.TokenMissingException;
import com.miniinfomates2003.asset_management.repositories.ActivoRepository;
import com.miniinfomates2003.asset_management.security.SecurityConfguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@Transactional
public class ActivoService {

    private final ActivoRepository activoRepository;
    private final CuentaService cuentaService;

    @Autowired
    public ActivoService(ActivoRepository activoRepository, CuentaService cuentaService) {
        this.activoRepository = activoRepository;
        this.cuentaService = cuentaService;
    }

    public Activo aniadirActivo(Activo activo, Integer idCuenta) {
        // Comprobar que el usuario tenga acceso a la cuenta en la que quiere crear el Activo
        var usuario = SecurityConfguration.getAuthenticatedUser()
                .orElseThrow(TokenMissingException::new);

        var usuariosAsociados = cuentaService.getUsuariosAsociadosACuenta(idCuenta)
                .orElseThrow(NoAccessException::new);

        if (usuariosAsociados.stream().noneMatch(u -> u.getId().toString().equals(usuario.getUsername()))) {
            throw new NoAccessException();
        }
        // Comprobar que en la cuenta en la que queremos crear el activo no se ha llegado al máximo de activos permitidos
        var maxNumActivos = cuentaService.getMaxNumActivosPermitidos(idCuenta).orElseThrow(NoAccessException::new);
        System.out.println("Número máximo de activos permitidos: " + maxNumActivos);
        var activos = activoRepository.findByIdCuenta(idCuenta);
        var numActivosActualmente = activos.size();
        System.out.println("Número actual de activos: " + activos.size());
        if (maxNumActivos.equals(numActivosActualmente)) {
            throw new NoAccessException();
        }
        // Inicializar colecciones si son nulas
        if (activo.getCategorias() == null) {
            activo.setCategorias(new HashSet<>());
        }
        if (activo.getIdProductos() == null) {
            activo.setIdProductos(new HashSet<>());
        }
        activo.setId(null);
        return activoRepository.save(activo);
    }
}
