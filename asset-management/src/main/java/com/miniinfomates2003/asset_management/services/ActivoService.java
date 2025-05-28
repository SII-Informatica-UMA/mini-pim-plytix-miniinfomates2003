package com.miniinfomates2003.asset_management.services;

import com.miniinfomates2003.asset_management.controllers.Mapper;
import com.miniinfomates2003.asset_management.dtos.ActivoDTO;
import com.miniinfomates2003.asset_management.entities.Activo;
import com.miniinfomates2003.asset_management.entities.Categoria;
import com.miniinfomates2003.asset_management.entities.Usuario;
import com.miniinfomates2003.asset_management.exceptions.NoAccessException;
import com.miniinfomates2003.asset_management.exceptions.NotFoundException;
import com.miniinfomates2003.asset_management.exceptions.TokenMissingException;
import com.miniinfomates2003.asset_management.repositories.ActivoRepository;
import com.miniinfomates2003.asset_management.repositories.CategoriaRepository;
import com.miniinfomates2003.asset_management.security.SecurityConfguration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
public class ActivoService {

    private final ActivoRepository activoRepository;
    private final CategoriaRepository categoriaRepository;
    private final CuentaService cuentaService;

    @Autowired
    public ActivoService(ActivoRepository activoRepository, CuentaService cuentaService,
                         CategoriaRepository categoriaRepository) {
        this.activoRepository = activoRepository;
        this.cuentaService = cuentaService;
        this.categoriaRepository = categoriaRepository;
    }

    public boolean isAdmin(UserDetails user) {
        return user.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals(Usuario.Rol.ADMINISTRADOR.name()));
    }

    public boolean hasPermissionToUpdate(Integer idActivo) {
        try {
            var usuario = SecurityConfguration.getAuthenticatedUser()
                    .orElseThrow(TokenMissingException::new);

            Activo activo = activoRepository.findById(idActivo)
                    .orElseThrow(NotFoundException::new);

            var usuariosAsociados = cuentaService.getUsuariosAsociadosACuenta(activo.getIdCuenta())
                    .orElseThrow(NotFoundException::new);

            if (usuariosAsociados.stream().noneMatch(u -> u.getId().toString().equals(usuario.getUsername()))
                    && !isAdmin(usuario)) {
                throw new NoAccessException();
            }

            return true;
        } catch (NoAccessException | TokenMissingException e) {
            return false;
        }
    }

    public Activo aniadirActivo(Activo activo, Integer idCuenta) {
        // Comprobar que el usuario tenga acceso a la cuenta en la que quiere crear el Activo
        var usuario = SecurityConfguration.getAuthenticatedUser()
                .orElseThrow(TokenMissingException::new);

        var usuariosAsociados = cuentaService.getUsuariosAsociadosACuenta(idCuenta)
                .orElseThrow(NotFoundException::new);

        if (usuariosAsociados.stream().noneMatch(u -> u.getId().toString().equals(usuario.getUsername()))
                && !isAdmin(usuario)) {
            throw new NoAccessException();
        }
        // Comprobar que en la cuenta en la que queremos crear el activo no se ha llegado al máximo de activos permitidos
        var maxNumActivos = cuentaService.getMaxNumActivosPermitidos(idCuenta).orElseThrow(NoAccessException::new);
        // System.out.println("Número máximo de activos permitidos: " + maxNumActivos);
        var activos = activoRepository.findByIdCuenta(idCuenta);
        var numActivosActualmente = activos.size();
        // System.out.println("Número actual de activos: " + activos.size());
        if (maxNumActivos.equals(numActivosActualmente)) {
            throw new NoAccessException();
        }

        activo.setId(null);
        Activo savedActivo = activoRepository.save(activo);

        // Actualizar la relación bidireccional en Categoria (propietaria de la relacion)
        if (!activo.getCategorias().isEmpty()) {
            Set<Categoria> categoriasActualizadas = new HashSet<>();
            for (Categoria categoria : activo.getCategorias()) {
                // Aquí es necesario cargar la categoría desde la base de datos
                // para poder modificar su colección de activos
                Categoria managedCategoria = categoriaRepository.findById(categoria.getId())
                        .orElseThrow(NotFoundException::new);

                categoriasActualizadas.add(managedCategoria);

                managedCategoria.getActivos().add(savedActivo);
                categoriaRepository.save(managedCategoria);
            }
            savedActivo.setCategorias(categoriasActualizadas);
        }
        return savedActivo;
    }

    public Optional<Activo> obtenerPorActivo(Integer idActivo) {
        var usuario = SecurityConfguration.getAuthenticatedUser()
                .orElseThrow(TokenMissingException::new);
        Optional<Activo> activo = activoRepository.findById(idActivo);

        if (activo.isEmpty())
            throw new NotFoundException();

        var usuariosAsociados = cuentaService.getUsuariosAsociadosACuenta(activo.get().getIdCuenta())
                .orElseThrow(NotFoundException::new);
        if (usuariosAsociados.stream().noneMatch(u -> u.getId().toString().equals(usuario.getUsername()))
                && !isAdmin(usuario))
            throw new NoAccessException();
        return activo;
    }

    public List<Activo> obtenerPorCuenta(Integer idCuenta) {
        var usuario = SecurityConfguration.getAuthenticatedUser()
                .orElseThrow(TokenMissingException::new);

        var usuariosAsociados = cuentaService.getUsuariosAsociadosACuenta(idCuenta)
                .orElseThrow(NotFoundException::new);

        if (usuariosAsociados.stream().noneMatch(u -> u.getId().toString().equals(usuario.getUsername()))
                && !isAdmin(usuario))
            throw new NoAccessException();

        return activoRepository.findByIdCuenta(idCuenta);
    }

    public List<Activo> obtenerPorCategoria(Integer idCategoria) {
        var usuario = SecurityConfguration.getAuthenticatedUser()
                .orElseThrow(TokenMissingException::new);

        List<Activo> activos = activoRepository.findByIdCategoria(idCategoria);

        if (activos.isEmpty())
            throw new NotFoundException();

        var usuariosAsociados = activos.stream()
                .map(Activo::getIdCuenta)
                .distinct()
                .map(idCuenta -> cuentaService.getUsuariosAsociadosACuenta(idCuenta)
                        .orElseThrow(NotFoundException::new))
                .flatMap(List::stream)
                .collect(Collectors.toList());
        if (usuariosAsociados.stream().noneMatch(u -> u.getId().toString().equals(usuario.getUsername()))
                && !isAdmin(usuario))
            throw new NoAccessException();
        return activos;
    }

    public List<Activo> obtenerPorProducto(Integer idProducto) {
        var usuario = SecurityConfguration.getAuthenticatedUser()
                .orElseThrow(TokenMissingException::new);

        List<Activo> activos = activoRepository.findByIdProductosContaining(idProducto);

        if (activos.isEmpty())
            throw new NotFoundException();

        List<Usuario> usuariosAsociados = activos.stream()
                .map(Activo::getIdCuenta)
                .distinct()
                .map(idCuenta -> cuentaService.getUsuariosAsociadosACuenta(idCuenta)
                        .orElseThrow(NotFoundException::new))
                .flatMap(List::stream)
                .collect(Collectors.toList());
        if (usuariosAsociados.stream().noneMatch(u -> u.getId().toString().equals(usuario.getUsername()))
                && !isAdmin(usuario))
            throw new NoAccessException();
        return activos;
    }

    @Transactional
    public Activo updateActivo(Integer idActivo, ActivoDTO activoDTO) {
        Activo activo = activoRepository.findById(idActivo)
                .orElseThrow(NotFoundException::new);

        if (!hasPermissionToUpdate(idActivo)) {
            throw new NoAccessException(); // Forbidden
        }

        // Updates the fields of the Activo entity with the values from ActivoDTO
        activo.setNombre(activoDTO.getNombre());
        activo.setTipo(activoDTO.getTipo());
        activo.setTamanio(activoDTO.getTamanio());
        activo.setUrl(activoDTO.getUrl());

        Set<Categoria> updatedCategorias = activoDTO.getCategorias().stream()
                .map(Mapper::toEntity)
                .collect(Collectors.toSet());

        activo.setCategorias(updatedCategorias);

        // activo.getCategorias() == null nunca se evaluará a true
        // en la linea previa hacemos activo.setCategorias(updatedCategorias);
        // por lo que, en el peor de los casos, sera una coleccion vacia

        if(!activo.getCategorias().isEmpty()){
            for(Categoria categoria : activo.getCategorias()) {
                Categoria managedCategoria = categoriaRepository.findById(categoria.getId())
                        .orElseThrow(NotFoundException::new);

                managedCategoria.getActivos().add(activo);
                categoriaRepository.save(managedCategoria);
            }
        }

        activo.setIdProductos(new HashSet<>(activoDTO.getProductos()));

        return activo;
    }

    public void deleteActivo(Integer idActivo) {

        Activo activo = activoRepository.findById(idActivo)
                .orElseThrow(NotFoundException::new);

        if (!hasPermissionToUpdate(idActivo)) {
            throw new NoAccessException(); // Forbidden
        }

        if (!activo.getCategorias().isEmpty()) {
            for (Categoria categoria : activo.getCategorias()) {
                Categoria managedCategoria = categoriaRepository.findById(categoria.getId())
                        .orElseThrow(NotFoundException::new);

                managedCategoria.getActivos().remove(activo);
                categoriaRepository.save(managedCategoria);

            }
        }

        activoRepository.delete(activo);
    }
}
