package com.miniinfomates2003.asset_management;

import com.miniinfomates2003.asset_management.dtos.ActivoDTO;
import com.miniinfomates2003.asset_management.dtos.CategoriaDTO;
import com.miniinfomates2003.asset_management.entities.Activo;
import com.miniinfomates2003.asset_management.entities.Categoria;
import com.miniinfomates2003.asset_management.repositories.ActivoRepository;
import com.miniinfomates2003.asset_management.repositories.CategoriaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Nested;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.util.DefaultUriBuilderFactory;
import org.springframework.web.util.UriBuilder;
import org.springframework.web.util.UriBuilderFactory;

import java.net.URI;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@DisplayName("En el servicio de gestion de activos y sus categorias")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class AssetManagementApplicationTests {

    @Autowired
    private TestRestTemplate restTemplate;

    @Value(value = "${local.server.port}")
    private int port;

    @Value(value = "${tokenAdmin}")
    private String tokenAdmin;

    @Value(value = "${tokenVictoria}")
    private String tokenVictoria;

    @Autowired
    private ActivoRepository activoRepository;

    @Autowired
    private CategoriaRepository categoriaRepository;

    @BeforeEach
    public void initializeDatabase() {
        activoRepository.deleteAll();
        categoriaRepository.deleteAll();
    }

    private URI uri(String scheme, String host, int port, String... paths) {
        UriBuilderFactory ubf = new DefaultUriBuilderFactory();
        UriBuilder ub = ubf.builder()
                .scheme(scheme)
                .host(host).port(port);
        for (String path : paths) {
            ub = ub.path(path);
        }
        return ub.build();
    }

    private RequestEntity<Void> get(String scheme, String host, int port, String path, String token) {
        URI uri = uri(scheme, host, port, path);
        var peticion = RequestEntity.get(uri)
                .accept(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + token)
                .build();
        return peticion;
    }

    private RequestEntity<Void> delete(String scheme, String host, int port, String path) {
        URI uri = uri(scheme, host, port, path);
        var peticion = RequestEntity.delete(uri)
                .build();
        return peticion;
    }

    private <T> RequestEntity<T> post(String scheme, String host, int port, String path, T object) {
        URI uri = uri(scheme, host, port, path);
        var peticion = RequestEntity.post(uri)
                .contentType(MediaType.APPLICATION_JSON)
                .body(object);
        return peticion;
    }

    private <T> RequestEntity<T> put(String scheme, String host, int port, String path, T object) {
        URI uri = uri(scheme, host, port, path);
        var peticion = RequestEntity.put(uri)
                .contentType(MediaType.APPLICATION_JSON)
                .body(object);
        return peticion;
    }

    private URI uriWithQueryParams(String scheme, String host, int port, String path, String paramName, List<Long> paramValues) {
        UriBuilderFactory ubf = new DefaultUriBuilderFactory();
        UriBuilder ub = ubf.builder()
                .scheme(scheme)
                .host(host)
                .port(port)
                .path(path);

        if (paramValues != null && !paramValues.isEmpty()) {
            for (Long value : paramValues) {
                ub = ub.queryParam(paramName, value);
            }
        }

        return ub.build();
    }

    private RequestEntity<Void> putWithQueryParams(String scheme, String host, int port, String path, String paramName, List<Long> paramValues) {
        URI uri = uriWithQueryParams(scheme, host, port, path, paramName, paramValues);
        var peticion = RequestEntity.put(uri)
                .contentType(MediaType.APPLICATION_JSON)
                .build();
        return peticion;
    }

    private RequestEntity<Void> deleteWithQueryParams(String scheme, String host, int port, String path, String paramName, List<Long> paramValues) {
        URI uri = uriWithQueryParams(scheme, host, port, path, paramName, paramValues);
        var peticion = RequestEntity.delete(uri)
                .build();
        return peticion;
    }

    private RequestEntity<Void> getWithQueryParams(String scheme, String host, int port, String path, String token, String paramName, List<Long> paramValues) {
        URI uri = uriWithQueryParams(scheme, host, port, path, paramName, paramValues);
        var peticion = RequestEntity.get(uri)
                .accept(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + token)
                .build();
        return peticion;
    }

    private <T> RequestEntity<T> postWithQueryParams(String scheme, String host, int port, String path, T object, String paramName, List<Long> paramValues) {
        URI uri = uriWithQueryParams(scheme, host, port, path, paramName, paramValues);
        var peticion = RequestEntity.post(uri)
                .contentType(MediaType.APPLICATION_JSON)
                .body(object);
        return peticion;
    }


    @Nested
    @DisplayName("cuando no hay activos")
    class ActivosVacio {
        @Test
        @DisplayName("devuelve la lista de activos vacía")
        public void devuelveLista() {
            List<Long> idCuentaValues = List.of(1L);
            var peticion = getWithQueryParams("http", "localhost", port, "/activo", tokenAdmin, "idCuenta", idCuentaValues);
            var respuesta = restTemplate.exchange(peticion,
                    new ParameterizedTypeReference<List<ActivoDTO>>() {});
            assertThat(respuesta.getStatusCode().value()).isEqualTo(200);
            assertThat(respuesta.getBody()).isEmpty();
        }
    }

    @Nested
    @DisplayName("cuando no hay categorias")
    class CategoriasVacias {
        @Test
        @DisplayName("devuelve la lista de categorías vacía a partir de un idCuenta")
        public void devuelveListaAPartirDeIdCuenta() {
            List<Long> idCuentaValues = List.of(1L);
            var peticion = getWithQueryParams("http", "localhost", port, "/categoria-activo", tokenAdmin, "idCuenta", idCuentaValues);
            var respuesta = restTemplate.exchange(peticion,
                    new ParameterizedTypeReference<List<CategoriaDTO>>() {});
            assertThat(respuesta.getStatusCode().value()).isEqualTo(200);
            assertThat(respuesta.getBody()).isEmpty();
        }

        @Test
        @DisplayName("devuelve error cuando se pide una categoría concreta")
        public void devuelveListaAPartirDeIdCategoria() {
            List<Long> idCategoriaValues = List.of(1L);
            var peticion = getWithQueryParams("http", "localhost", port, "/categoria-activo", tokenAdmin, "idCategoria", idCategoriaValues);
            var respuesta = restTemplate.exchange(peticion,
                    new ParameterizedTypeReference<List<CategoriaDTO>>() {});
            assertThat(respuesta.getStatusCode().value()).isEqualTo(404);
            assertThat(respuesta.hasBody()).isEqualTo(false);
        }
    }

    @Nested
    @DisplayName("cuando hay categorias")
    class HayCategorias {
        @BeforeEach
        public void introduceDatos() {
            categoriaRepository.save(new Categoria(null, "Especificaciones", 3, null));
        }

        @Test
        @DisplayName("devuelve una categoría concreta si tiene permiso")
        public void devuelveCategoria() {
            List<Long> idCategoriaValues = List.of(1L);
            var peticion = getWithQueryParams("http", "localhost", port, "/categoria-activo", tokenAdmin, "idCategoria", idCategoriaValues);
            var respuesta = restTemplate.exchange(peticion,
                    new ParameterizedTypeReference<List<CategoriaDTO>>() {});
            assertThat(respuesta.getStatusCode().value()).isEqualTo(200);
            assertThat(respuesta.hasBody()).isEqualTo(true);
            assertThat(respuesta.getBody()).isNotNull();
        }

        @Test
        @DisplayName("devuelve error si no se tiene acceso a la cuenta de la categoría")
        public void devuelveErrorCategoria() {
            List<Long> idCategoriaValues = List.of(1L);
            var peticion = getWithQueryParams("http", "localhost", port, "/categoria-activo", tokenVictoria, "idCategoria", idCategoriaValues);
            var respuesta = restTemplate.exchange(peticion,
                    new ParameterizedTypeReference<List<CategoriaDTO>>() {
                    });
            assertThat(respuesta.getStatusCode().value()).isEqualTo(403);
            assertThat(respuesta.hasBody()).isEqualTo(false);
        }
    }
}
