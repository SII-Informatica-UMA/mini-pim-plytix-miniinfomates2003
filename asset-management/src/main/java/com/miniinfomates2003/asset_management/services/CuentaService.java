package com.miniinfomates2003.asset_management.services;

import com.miniinfomates2003.asset_management.entities.Cuenta;
import com.miniinfomates2003.asset_management.entities.Usuario;
import com.miniinfomates2003.asset_management.security.JwtUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.RequestEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.Optional;

@Service
public class CuentaService {

    @Value("${baseURL}")
    private String baseURL;

    private JwtUtil jwtUtil;
    private Usuario usuarioApp;

    private RestTemplate restTemplate;


    public CuentaService(RestTemplate restTemplate, JwtUtil jwtUtil) {
        this.restTemplate = restTemplate;
        this.jwtUtil = jwtUtil;
        this.usuarioApp = Usuario.builder()
                .id(-1L)
                .nombre("Microservicio")
                .role(Usuario.Rol.ADMINISTRADOR)
                .build();
    }

    public Optional<List<Usuario>> getUsuariosAsociadosACuenta(Integer idCuenta) {
        var uri = UriComponentsBuilder.fromUriString(baseURL + "/cuenta/{idCuenta}/usuarios")
                .buildAndExpand(idCuenta)
                .toUri();

        var appJwtToken = jwtUtil.generateToken(usuarioApp);

        var peticion = RequestEntity.get(uri)
                .header("Authorization", "Bearer " + appJwtToken)
                .build();
        try {
            return Optional.ofNullable(this.restTemplate.exchange(
                    peticion,
                    new ParameterizedTypeReference<List<Usuario>>() {}
            ).getBody());
        } catch (Exception e) {
            //e.printStackTrace();
            return Optional.empty();
        }
    }

    public Optional<Integer> getMaxNumActivosPermitidos(Integer idCuenta) {
        var uri = UriComponentsBuilder.fromUriString(baseURL + "/cuenta")
                .queryParam("idCuenta", idCuenta)
                .build()
                .toUri();
        var appJwtToken = jwtUtil.generateToken(usuarioApp);

        var peticion = RequestEntity.get(uri)
                .header("Authorization", "Bearer " + appJwtToken)
                .build();
        try {
            return Optional.ofNullable(this.restTemplate.exchange(peticion, Cuenta[].class).getBody()[0].getPlan().getMaxActivos());
        }
        catch (Exception e) {
            return Optional.empty();
        }
    }

    public Optional<Integer> getMaxNumCategoriasActivosPermitidos(Integer idCuenta) {
        var uri = UriComponentsBuilder.fromUriString(baseURL + "/cuenta")
                .queryParam("idCuenta", idCuenta)
                .build()
                .toUri();
        var appJwtToken = jwtUtil.generateToken(usuarioApp);

        var peticion = RequestEntity.get(uri)
                .header("Authorization", "Bearer" + appJwtToken)
                .build();
        try {
            return Optional.ofNullable(this.restTemplate.exchange(peticion, Cuenta[].class).getBody()[0].getPlan().getMaxCategoriasActivos());
        } catch (Exception e) {
            return Optional.empty();
        }
    }
}
