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
        // Comprobar que exista la categoría
        if (categoria.isEmpty())
        {
            throw new NotFoundException();
        }
        // Comprobar que el usuario tenga acceso a la cuenta a la que pertenece la categoria
        var usuariosAsociados = cuentaService.getUsuariosAsociadosACuenta(categoria.get().getIdCuenta())
                .orElseThrow(NoAccessException::new);
        if (usuariosAsociados.stream().noneMatch(u -> u.getId().toString().equals(usuario.getUsername()))) {
            throw new NoAccessException();
        }
        return categoria;
    }

    public List<Categoria> obtenerPorCuenta(Integer idCuenta) {
        // Comprobar que el usuario tenga acceso a la cuenta de la que quiere obtener la categoria
        var usuario = SecurityConfguration.getAuthenticatedUser()
                .orElseThrow(TokenMissingException::new);

        var usuariosAsociados = cuentaService.getUsuariosAsociadosACuenta(idCuenta)
                .orElseThrow(NoAccessException::new);

        if (usuariosAsociados.stream().noneMatch(u -> u.getId().toString().equals(usuario.getUsername()))) {
            throw new NoAccessException();
        }

        return categoriaRepository.findByIdCuenta(idCuenta);
    }

    public boolean hasPermissionToUpdate(Integer idCategoria) {
        try {
            // Obtenemos el usuario autenticado
            var usuario = SecurityConfguration.getAuthenticatedUser()
                    .orElseThrow(TokenMissingException::new);

            // Extraemos la categoría con id idCategoria
            Categoria categoria = categoriaRepository.findById(idCategoria);

            // Comprobamos si existe una categoria cuyo id sea idCategoria
            if (categoria.isEmpty()) {
                // No existe una categoria cuyo id sea idCategoria
                throw new RuntimeException("No existe una categoría cuyo id sea " + idCategoria + ".");
            } else {
                // Existe una categoria cuyo id sea idCategoria
                // Extraemos los usuarios asociados a la cuenta de la categoría
                var usuariosAsociados = cuentaService.getUsuariosAsociadosACuenta(activo.getIdCuenta())
                        .orElseThrow(NoAccessException::new);

                // Comprobamos si el usuario autenticado se encuentra en la lista de usuarios con permisos
                if (usuariosAsociados.stream().noneMatch(u -> u.getId().toString().equals(usuario.getUsername()))) {
                    // El usuario autenticado no se encuentra en la lista de usuarios con permisos
                    throw new NoAccessException();
                } else {
                    // El usuario autenticado se encuentra en la lista de usuarios con permisos
                    return true;
                }
            }
        } catch (NoAccessException | TokenMissingException e) {
            return false;
        }
    }
}