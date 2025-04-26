package com.miniinfomates2003.asset_management.services;

import com.miniinfomates2003.asset_management.entities.Categoria;
import com.miniinfomates2003.asset_management.repositories.CategoriaRepository;
import com.miniinfomates2003.asset_management.security.SecurityConfguration;
import com.miniinfomates2003.asset_management.exceptions.NoAccessException;
import com.miniinfomates2003.asset_management.exceptions.TokenMissingException;
import com.miniinfomates2003.asset_management.exceptions.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class CategoriaService {

    private final CategoriaRepository categoriaRepository;
    private final CuentaService cuentaService;

    @Autowired
    public CategoriaService(CategoriaRepository categoriaRepository, CuentaService cuentaService) {
        this.categoriaRepository = categoriaRepository;
        this.cuentaService = cuentaService;
    }

    public Optional<Categoria> obtenerPorCategoria(Integer idCategoria) {
        var usuario = SecurityConfguration.getAuthenticatedUser()
                .orElseThrow(TokenMissingException::new);
        Optional<Categoria> categoria = categoriaRepository.findById(idCategoria);
        if (categoria.isEmpty())
        {
            throw new NotFoundException();
        }
        var usuariosAsociados = cuentaService.getUsuariosAsociadosACuenta(categoria.get().getIdCuenta())
                .orElseThrow(NoAccessException::new);
        if (usuariosAsociados.stream().noneMatch(u -> u.getId().toString().equals(usuario.getUsername()))) {
            throw new NoAccessException();
        }
        return categoria;
    }

    public List<Categoria> obtenerPorCuenta(Integer idCuenta) {
        var usuario = SecurityConfguration.getAuthenticatedUser()
                .orElseThrow(TokenMissingException::new);

        var usuariosAsociados = cuentaService.getUsuariosAsociadosACuenta(idCuenta)
                .orElseThrow(NoAccessException::new);

        if (usuariosAsociados.stream().noneMatch(u -> u.getId().toString().equals(usuario.getUsername()))) {
            throw new NoAccessException();
        }

        return categoriaRepository.findByIdCuenta(idCuenta);
    }
}