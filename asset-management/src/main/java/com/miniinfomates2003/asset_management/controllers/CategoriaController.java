package com.miniinfomates2003.asset_management.controllers;
import com.miniinfomates2003.asset_management.dtos.CategoriaDTO;
import com.miniinfomates2003.asset_management.entities.Categoria;
import com.miniinfomates2003.asset_management.entities.Usuario;
import com.miniinfomates2003.asset_management.security.SecurityConfguration;
import com.miniinfomates2003.asset_management.services.CategoriaService;
import com.miniinfomates2003.asset_management.services.CuentaService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/categoria-activo")
public class CategoriaController {
    private final CategoriaService categoriaService;
    private final CuentaService cuentaService;

    @Autowired
    public CategoriaController(CategoriaService categoriaService, CuentaService cuentaService) {
        this.categoriaService = categoriaService;
        this.cuentaService = cuentaService;
    }

    @GetMapping
    public ResponseEntity<List<CategoriaDTO>> obtenerCategorias(@RequestParam(required = false) Integer idCuenta,
                                                @RequestParam(required = false) Integer idCategoria,
                                                @RequestHeader("Authorization") String jwtToken) {
        var usuario = SecurityConfguration.getAuthenticatedUser()
                .orElse(null);
        if (usuario == null) {
            // Si no hay usuario autenticado, devolver un 401 Unauthorized
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .header("Error-Message", "Credenciales no válidas o faltantes")
                    .build();
        }

        if (idCuenta != null) {
            Optional<List<Usuario>> usuariosAsociadosOpt = cuentaService.getUsuariosAsociadosACuenta(idCuenta, jwtToken);
            if (usuariosAsociadosOpt.isEmpty() || usuariosAsociadosOpt.get().stream().noneMatch(u -> u.getId().toString().equals(usuario.getUsername()))) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .header("Error-Message", "Sin permisos suficientes")
                        .build();
            }
            return ResponseEntity.ok(categoriaService.obtenerPorCuenta(idCuenta).stream()
                    .map(Mapper::toDTO)
                    .collect(Collectors.toList()));
        } else if(idCategoria != null) {
            Optional<Categoria> categoriaOpt= categoriaService.obtenerPorCategoria(idCategoria);
            return categoriaOpt
                    .map(Mapper::toDTO)
                    .map((CategoriaDTO dto) -> {
                        List<CategoriaDTO> lista = List.of(dto);
                        return ResponseEntity.ok(lista);
                    })
                    .orElse(ResponseEntity.notFound().build());
        } else {
            return ResponseEntity.badRequest().build();
        }
    }
}
