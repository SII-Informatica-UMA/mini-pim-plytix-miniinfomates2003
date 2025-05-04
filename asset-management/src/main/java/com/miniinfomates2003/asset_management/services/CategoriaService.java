package com.miniinfomates2003.asset_management.services;

import com.miniinfomates2003.asset_management.entities.Activo;
import com.miniinfomates2003.asset_management.entities.Categoria;
import com.miniinfomates2003.asset_management.entities.Usuario;
import com.miniinfomates2003.asset_management.repositories.ActivoRepository;
import com.miniinfomates2003.asset_management.repositories.CategoriaRepository;
import com.miniinfomates2003.asset_management.security.SecurityConfguration;
import com.miniinfomates2003.asset_management.exceptions.NoAccessException;
import com.miniinfomates2003.asset_management.exceptions.TokenMissingException;
import com.miniinfomates2003.asset_management.exceptions.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class CategoriaService {

    private final CategoriaRepository categoriaRepository;
    private final ActivoRepository activoRepository;
    private final CuentaService cuentaService;

    @Autowired
    public CategoriaService(CategoriaRepository categoriaRepository, ActivoRepository activoRepository, CuentaService cuentaService) {
        this.categoriaRepository = categoriaRepository;
        this.activoRepository = activoRepository;
        this.cuentaService = cuentaService;
    }

    public boolean isAdmin(UserDetails user) {
        return user.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals(Usuario.Rol.ADMINISTRADOR.name()));
    }

    public Categoria aniadirCategoria(Categoria categoria, Integer idCuenta) {
        var usuario = SecurityConfguration.getAuthenticatedUser()
                .orElseThrow(TokenMissingException::new);

        var usuariosAsociados = cuentaService.getUsuariosAsociadosACuenta(idCuenta)
                .orElseThrow(NotFoundException::new);

        if (usuariosAsociados.stream().noneMatch(u -> u.getId().toString().equals(usuario.getUsername()))
                && !isAdmin(usuario))
            throw new NoAccessException();

        var maxNumCategorias = cuentaService.getMaxNumCategoriasActivosPermitidos(idCuenta).orElseThrow(NoAccessException::new);
        // System.out.println("Número máximo de categorías permitidos: " + maxNumCategorias);
        var categorias = categoriaRepository.findByIdCuenta(idCuenta);
        var numCategoriasActualmente = categorias.size();
        // System.out.println("Número actual de categorías: " + numCategoriasActualmente);
        if (maxNumCategorias.equals(numCategoriasActualmente))
            throw new NoAccessException();

        if (categoria.getActivos() == null)
            categoria.setActivos(new HashSet<>());
        categoria.setId(null);
        Categoria savedCategoria = categoriaRepository.save(categoria);

        if (categoria.getActivos() != null && !categoria.getActivos().isEmpty()) {
            for (Activo activo : categoria.getActivos()) {
                Activo managedActivo = activoRepository.findById(activo.getId())
                        .orElseThrow(NotFoundException::new);

                if (managedActivo.getCategorias() == null)
                    managedActivo.setCategorias(new HashSet<>());

                managedActivo.getCategorias().add(savedCategoria);
                activoRepository.save(managedActivo);
            }
        }

        return savedCategoria;
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
                .orElseThrow(NotFoundException::new);
        if (usuariosAsociados.stream().noneMatch(u -> u.getId().toString().equals(usuario.getUsername()))
                && !isAdmin(usuario)) {
            throw new NoAccessException();
        }
        return categoria;
    }

    public List<Categoria> obtenerPorCuenta(Integer idCuenta) {
        // Comprobar que el usuario tenga acceso a la cuenta de la que quiere obtener la categoria
        var usuario = SecurityConfguration.getAuthenticatedUser()
                .orElseThrow(TokenMissingException::new);

        var usuariosAsociados = cuentaService.getUsuariosAsociadosACuenta(idCuenta)
                .orElseThrow(NotFoundException::new);

        if (usuariosAsociados.stream().noneMatch(u -> u.getId().toString().equals(usuario.getUsername()))
                && !isAdmin(usuario)) {
            throw new NoAccessException();
        }

        return categoriaRepository.findByIdCuenta(idCuenta);
    }

    public Categoria updateCategoria(Integer idCategoria, Categoria categoria) {
        // Obtenemos el usuario autenticado
        var usuario = SecurityConfguration.getAuthenticatedUser()
                .orElseThrow(TokenMissingException::new);

        // Extraemos la categoría con id idCategoria
        Optional<Categoria> categoriaActual = categoriaRepository.findById(idCategoria);

        // Comprobamos si existe una categoria cuyo id sea idCategoria
        if (categoriaActual.isEmpty()) {
            // No existe una categoria cuyo id sea idCategoria
            throw new NotFoundException();
        } else {
            // Existe una categoria cuyo id sea idCategoria
            // Extraemos los usuarios asociados a la cuenta de la categoría
            var usuariosAsociados = cuentaService.getUsuariosAsociadosACuenta(categoriaActual.get().getIdCuenta())
                    .orElseThrow(NotFoundException::new);

            // Comprobamos si el usuario autenticado se encuentra en la lista de usuarios con permisos
            if (usuariosAsociados.stream().noneMatch(u -> u.getId().toString().equals(usuario.getUsername()))
                    && !isAdmin(usuario)) {
                // El usuario autenticado no se encuentra en la lista de usuarios con permisos
                throw new NoAccessException();
            } else {
                // El usuario autenticado se encuentra en la lista de usuarios con permisos
                categoria.setIdCuenta(categoriaActual.get().getIdCuenta());
                categoria.setActivos(categoriaActual.get().getActivos());
                return categoriaRepository.save(categoria);
            }
        }
    }

    public void deleteCategoria(Integer idCategoria) {
        // Obtenemos el usuario autenticado
        var usuario = SecurityConfguration.getAuthenticatedUser()
                .orElseThrow(TokenMissingException::new);

        // Extraemos la categoría con id idCategoria
        Optional<Categoria> categoriaActual = categoriaRepository.findById(idCategoria);

        // Comprobamos si existe una categoria cuyo id sea idCategoria
        if (categoriaActual.isEmpty()) {
            // No existe una categoria cuyo id sea idCategoria
            throw new NotFoundException();
        } else {
            // Existe una categoria cuyo id sea idCategoria
            // Extraemos los usuarios asociados a la cuenta de la categoría
            var usuariosAsociados = cuentaService.getUsuariosAsociadosACuenta(categoriaActual.get().getIdCuenta())
                    .orElseThrow(NotFoundException::new);

            // Comprobamos si el usuario autenticado se encuentra en la lista de usuarios con permisos
            if (usuariosAsociados.stream().noneMatch(u -> u.getId().toString().equals(usuario.getUsername()))
                    && !isAdmin(usuario)) {
                // El usuario autenticado no se encuentra en la lista de usuarios con permisos
                throw new NoAccessException();
            } else {
                if (!categoriaActual.get().getActivos().isEmpty()) {
                    throw new NoAccessException();
                } else {
                    Categoria categoria = categoriaActual.get();

                    categoriaRepository.delete(categoria);
                }
            }
        }
    }
}