package com.miniinfomates2003.asset_management.services;

import com.miniinfomates2003.asset_management.entities.Usuario;
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

    private RestTemplate restTemplate;
    public CuentaService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public Optional<List<Usuario>> getUsuariosAsociadosACuenta(Integer idCuenta, String jwtToken) {
        var uri = UriComponentsBuilder.fromUriString(baseURL + "/cuenta/{idCuenta}/usuarios")
                .buildAndExpand(idCuenta)
                .toUri();
        var peticion = RequestEntity.get(uri)
                .header("Authorization", jwtToken)
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
}
