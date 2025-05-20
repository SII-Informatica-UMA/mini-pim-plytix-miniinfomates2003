package com.miniinfomates2003.asset_management;

import com.miniinfomates2003.asset_management.dtos.ActivoDTO;
import com.miniinfomates2003.asset_management.dtos.CategoriaDTO;
import com.miniinfomates2003.asset_management.entities.Activo;
import com.miniinfomates2003.asset_management.entities.Categoria;
import com.miniinfomates2003.asset_management.repositories.ActivoRepository;
import com.miniinfomates2003.asset_management.repositories.CategoriaRepository;
import com.miniinfomates2003.asset_management.services.CuentaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Nested;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;
import org.springframework.web.util.UriBuilder;
import org.springframework.web.util.UriBuilderFactory;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@DisplayName("En el servicio de gestion de activos y sus categorias")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class AssetManagementApplicationTests {

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Autowired
    private RestTemplate restTemplate;
    private MockRestServiceServer mockServer;

    @Value(value = "${local.server.port}")
    private int port;

    @Value(value = "${tokenAdmin}")
    private String tokenAdmin;

    @Value(value = "${tokenVictoria}")
    private String tokenVictoria;

    @Value(value = "${baseURL}")
    private String baseURL;

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

    private RequestEntity<Void> putWithQueryParams(String scheme, String host, int port, String path, String token, String paramName, List<Long> paramValues) {
        URI uri = uriWithQueryParams(scheme, host, port, path, paramName, paramValues);
        var peticion = RequestEntity.put(uri)
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + token)
                .build();
        return peticion;
    }

    private RequestEntity<Void> deleteWithQueryParams(String scheme, String host, int port, String path, String token, String paramName, List<Long> paramValues) {
        URI uri = uriWithQueryParams(scheme, host, port, path, paramName, paramValues);
        var peticion = RequestEntity.delete(uri)
                .header("Authorization", "Bearer " + token)
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

    private <T> RequestEntity<T> postWithQueryParams(String scheme, String host, int port, String path,  String token, T object, String paramName, List<Long> paramValues) {
        URI uri = uriWithQueryParams(scheme, host, port, path, paramName, paramValues);
        var peticion = RequestEntity.post(uri)
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + token)
                .body(object);
        return peticion;
    }

    public void simulaRespuestaUsuariosCuentaUno() {
        var uriRemota = UriComponentsBuilder.fromUriString(baseURL + "/cuenta/1/usuarios")
                .build()
                .toUri();
        mockServer.expect(requestTo(uriRemota))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(
                                """
                                [
                                  {
                                    "nombre": "Antonio",
                                    "apellido1": "García",
                                    "apellido2": "Ramos",
                                    "email": "antonio@uma.es",
                                    "role": "CLIENTE",
                                    "id": 2
                                  },
                                  {
                                    "nombre": "Victoria",
                                    "apellido1": "Rodríguez",
                                    "apellido2": "Fernández",
                                    "email": "victoria@uma.es",
                                    "role": "CLIENTE",
                                    "id": 3
                                  }
                                ]
                                """
                        )
                );
    }

    public void simulaRespuestaUsuariosCuentaTres() {
        var uriRemota = UriComponentsBuilder.fromUriString(baseURL + "/cuenta/3/usuarios")
                .build()
                .toUri();
        mockServer.expect(requestTo(uriRemota))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(
                                """
                                [
                                  {
                                    "nombre": "Antonio",
                                    "apellido1": "García",
                                    "apellido2": "Ramos",
                                    "email": "antonio@uma.es",
                                    "role": "CLIENTE",
                                    "id": 2
                                  }
                                ]
                                """
                        )
                );
    }

    public void simulaRespuestaUsuariosCuentaInexistente() {
        var uriRemota = UriComponentsBuilder.fromUriString(baseURL + "/cuenta/99/usuarios")
                .build()
                .toUri();
        mockServer.expect(requestTo(uriRemota))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.NOT_FOUND));
    }

    public void simulaRespuestaMaxNumActivosCuentaUno() {
        var uriRemota = UriComponentsBuilder.fromUriString(baseURL + "/cuenta")
                .queryParam("idCuenta", 1)
                .build()
                .toUri();
        mockServer.expect(requestTo(uriRemota))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(
                        """
                                [
                                  {
                                    "id": 1,
                                    "nombre": "Cuenta 1",
                                    "direccion": "Calle Ficticia 123, Ciudad Ficticia",
                                    "nif": "12345678B",
                                    "fechaAlta": "2023-01-15",
                                    "plan": {
                                      "id": 1,
                                      "nombre": "Plan Básico",
                                      "maxProductos": 5,
                                      "maxActivos": 5,
                                      "maxAlmacenamiento": 5,
                                      "maxCategoriasProductos": 3,
                                      "maxCategoriasActivos": 3,
                                      "maxRelaciones": 1,
                                      "precio": 9.99
                                    }
                                  }
                                ]
                                """
                        ));
    }

    public void aniadeCuatroActivosCuentaUno() {
        Activo activo2 = Activo.builder()
                .nombre("Activo 2")
                .tipo("PDF")
                .tamanio(2)
                .url("https://mallba3.lcc.uma.es/activos/activo2.pdf")
                .idCuenta(1)
                .build();
        activoRepository.save(activo2);
        Activo activo3 = Activo.builder()
                .nombre("Activo 3")
                .tipo("PDF")
                .tamanio(2)
                .url("https://mallba3.lcc.uma.es/activos/activo3.pdf")
                .idCuenta(1)
                .build();
        activoRepository.save(activo3);
        Activo activo4 = Activo.builder()
                .nombre("Activo 4")
                .tipo("PDF")
                .tamanio(2)
                .url("https://mallba3.lcc.uma.es/activos/activo4.pdf")
                .idCuenta(1)
                .build();
        activoRepository.save(activo4);
        Activo activo5 = Activo.builder()
                .nombre("Activo 5")
                .tipo("PDF")
                .tamanio(2)
                .url("https://mallba3.lcc.uma.es/activos/activo5.pdf")
                .idCuenta(1)
                .build();
        activoRepository.save(activo5);
    }

    @BeforeEach
    public void setUpMockServer() {
        mockServer = MockRestServiceServer.createServer(restTemplate);
    }

    @Nested
    @DisplayName("cuando no hay activos")
    class ActivosVacio {
        @Test
        @DisplayName("devuelve la lista de activos vacía")
        public void devuelveLista() {
            simulaRespuestaUsuariosCuentaUno();
            List<Long> idCuentaValues = List.of(1L);
            var peticion = getWithQueryParams("http", "localhost", port, "/activo", tokenAdmin, "idCuenta", idCuentaValues);
            var respuesta = testRestTemplate.exchange(peticion,
                    new ParameterizedTypeReference<List<ActivoDTO>>() {
                    });
            assertThat(respuesta.getStatusCode().value()).isEqualTo(200);
            assertThat(respuesta.getBody()).isEmpty();
        }
        @Test
        @DisplayName("devuelve error al intentar crear un activo en una cuenta inexistente")
        public void devuelveError() {
            simulaRespuestaUsuariosCuentaInexistente();
            Activo activo = Activo.builder()
                    .nombre("Imagen del ordenador")
                    .tipo("JPG")
                    .tamanio(1)
                    .url("https://mallba3.lcc.uma.es/activos/imagen-ordenador.jpg")
                    .build();
            var peticion = postWithQueryParams("http", "localhost", port, "/activo", tokenVictoria, activo, "idCuenta", List.of(99L));
            var respuesta = testRestTemplate.exchange(peticion,
                    new ParameterizedTypeReference<ActivoDTO>() {});
            assertThat(respuesta.getStatusCode().value()).isEqualTo(404);
            assertThat(respuesta.hasBody()).isEqualTo(false);
        }
        @Test
        @DisplayName("se crea correctamente un activo cuando sus categorías y productos están a null")
        public void creaActivo() {
            simulaRespuestaUsuariosCuentaUno();
            simulaRespuestaMaxNumActivosCuentaUno();
            Activo activo = Activo.builder()
                    .nombre("Imagen del ordenador")
                    .tipo("JPG")
                    .tamanio(1)
                    .url("https://mallba3.lcc.uma.es/activos/imagen-ordenador.jpg")
                    .categorias(null)
                    .idProductos(null)
                    .build();
            var peticion = postWithQueryParams("http", "localhost", port, "/activo", tokenVictoria, activo, "idCuenta", List.of(1L));
            var respuesta = testRestTemplate.exchange(peticion,
                    new ParameterizedTypeReference<ActivoDTO>() {});
            assertThat(respuesta.getStatusCode().value()).isEqualTo(201);
            assertThat(respuesta.getBody()).isNotNull();
        }
    }

    @Nested
    @DisplayName("cuando hay activos")
    class HayActivos {
        @BeforeEach
        public void introduceDatos() {
            Activo activo = Activo.builder()
                    .nombre("Manual del televisor")
                    .tipo("PDF")
                    .tamanio(2)
                    .url("https://mallba3.lcc.uma.es/activos/manual-televisor.pdf")
                    .idCuenta(1)
                    .build();
            activoRepository.save(activo);
        }
        @Test
        @DisplayName("permite crear un nuevo activo si se tiene acceso a la cuenta y no se excede el número permitido")
        public void creaActivo() {
            simulaRespuestaUsuariosCuentaUno();
            simulaRespuestaMaxNumActivosCuentaUno();
            Activo activo = Activo.builder()
                    .nombre("Imagen del ordenador")
                    .tipo("JPG")
                    .tamanio(1)
                    .url("https://mallba3.lcc.uma.es/activos/imagen-ordenador.jpg")
                    .build();
            var peticion = postWithQueryParams("http", "localhost", port, "/activo", tokenVictoria, activo, "idCuenta", List.of(1L));
            var respuesta = testRestTemplate.exchange(peticion,
                    new ParameterizedTypeReference<ActivoDTO>() {});
            assertThat(respuesta.getStatusCode().value()).isEqualTo(201);
            assertThat(respuesta.getBody()).isNotNull();
        }
        @Test
        @DisplayName("al intentar crear un nuevo activo devuelve error si no se tiene acceso a la cuenta")
        public void devuelveError() {
            simulaRespuestaUsuariosCuentaTres();
            Activo activo = Activo.builder()
                    .nombre("Imagen del ordenador")
                    .tipo("JPG")
                    .tamanio(1)
                    .url("https://mallba3.lcc.uma.es/activos/imagen-ordenador.jpg")
                    .build();
            var peticion = postWithQueryParams("http", "localhost", port, "/activo", tokenVictoria, activo, "idCuenta", List.of(3L));
            var respuesta = testRestTemplate.exchange(peticion,
                    new ParameterizedTypeReference<ActivoDTO>() {});
            assertThat(respuesta.getStatusCode().value()).isEqualTo(403);
            assertThat(respuesta.hasBody()).isEqualTo(false);
        }
        @Test
        @DisplayName("devuelve error si se intenta crear un nuevo activo y se excede el número permitido")
        public void devuelveErrorMaxActivos() {
            aniadeCuatroActivosCuentaUno();
            simulaRespuestaUsuariosCuentaUno();
            simulaRespuestaMaxNumActivosCuentaUno();
            Activo activo = Activo.builder()
                    .nombre("Activo no permitido")
                    .tipo("PDF")
                    .tamanio(2)
                    .url("https://mallba3.lcc.uma.es/activos/activo.pdf")
                    .idCuenta(1)
                    .build();
            var peticion = postWithQueryParams("http", "localhost", port, "/activo", tokenVictoria, activo, "idCuenta", List.of(1L));
            var respuesta = testRestTemplate.exchange(peticion,
                    new ParameterizedTypeReference<ActivoDTO>() {});
            assertThat(respuesta.getStatusCode().value()).isEqualTo(403);
            assertThat(respuesta.hasBody()).isEqualTo(false);
        }
    }

    @Nested
    @DisplayName("cuando no hay categorias")
    class CategoriasVacias {
        @BeforeEach
        public void setUp() {
            simulaRespuestaUsuariosCuentaUno();
        }

        @Test
        @DisplayName("devuelve la lista de categorías vacía a partir de un idCuenta")
        public void devuelveListaAPartirDeIdCuenta() {
            List<Long> idCuentaValues = List.of(1L);
            var peticion = getWithQueryParams("http", "localhost", port, "/categoria-activo", tokenAdmin, "idCuenta", idCuentaValues);
            var respuesta = testRestTemplate.exchange(peticion,
                    new ParameterizedTypeReference<List<CategoriaDTO>>() {});
            assertThat(respuesta.getStatusCode().value()).isEqualTo(200);
            assertThat(respuesta.getBody()).isEmpty();
        }

        @Test
        @DisplayName("devuelve error cuando se pide una categoría concreta")
        public void devuelveListaAPartirDeIdCategoria() {
            List<Long> idCategoriaValues = List.of(1L);
            var peticion = getWithQueryParams("http", "localhost", port, "/categoria-activo", tokenAdmin, "idCategoria", idCategoriaValues);
            var respuesta = testRestTemplate.exchange(peticion,
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
            simulaRespuestaUsuariosCuentaTres();
            List<Long> idCategoriaValues = List.of(1L);
            var peticion = getWithQueryParams("http", "localhost", port, "/categoria-activo", tokenAdmin, "idCategoria", idCategoriaValues);
            var respuesta = testRestTemplate.exchange(peticion,
                    new ParameterizedTypeReference<List<CategoriaDTO>>() {});
            assertThat(respuesta.getStatusCode().value()).isEqualTo(200);
            assertThat(respuesta.hasBody()).isEqualTo(true);
            assertThat(respuesta.getBody()).isNotNull();
        }

        @Test
        @DisplayName("devuelve error si no se tiene acceso a la cuenta de una categoría concreta")
        public void devuelveErrorCategoria() {
            simulaRespuestaUsuariosCuentaTres();
            List<Long> idCategoriaValues = List.of(1L);
            var peticion = getWithQueryParams("http", "localhost", port, "/categoria-activo", tokenVictoria, "idCategoria", idCategoriaValues);
            var respuesta = testRestTemplate.exchange(peticion,
                    new ParameterizedTypeReference<List<CategoriaDTO>>() {
                    });
            assertThat(respuesta.getStatusCode().value()).isEqualTo(403);
            assertThat(respuesta.hasBody()).isEqualTo(false);
        }

        @Test
        @DisplayName("devuelve error 404 si no existe una categoría concreta")
        public void devuelveErrorCategoriaNoExiste() {
            simulaRespuestaUsuariosCuentaUno();
            List<Long> idCategoriaValues = List.of(5L);
            var peticion = getWithQueryParams("http", "localhost", port, "/categoria-activo", tokenAdmin, "idCategoria", idCategoriaValues);
            var respuesta = testRestTemplate.exchange(peticion,
                    new ParameterizedTypeReference<List<CategoriaDTO>>() {
                    });
            assertThat(respuesta.getStatusCode().value()).isEqualTo(404);
            assertThat(respuesta.hasBody()).isEqualTo(false);
        }

        @Test
        @DisplayName("devuelve categorías asociadas a una cuenta si se tiene acceso")
        public void devuelveCategorias() {
            simulaRespuestaUsuariosCuentaTres();
            List<Long> idCuentaValues = List.of(3L);
            var peticion = getWithQueryParams("http", "localhost", port, "/categoria-activo", tokenAdmin, "idCuenta", idCuentaValues);
            var respuesta = testRestTemplate.exchange(peticion,
                    new ParameterizedTypeReference<List<CategoriaDTO>>() {});
            assertThat(respuesta.getStatusCode().value()).isEqualTo(200);
            assertThat(respuesta.hasBody()).isEqualTo(true);
            assertThat(respuesta.getBody()).isNotNull();
        }
        @Test
        @DisplayName("devuelve error al intentar acceder a las categorías de una cuenta a la que no se tiene acceso")
        public void devuelveErrorCategoriasCuenta() {
            simulaRespuestaUsuariosCuentaTres();
            List<Long> idCuentaValues = List.of(3L);
            var peticion = getWithQueryParams("http", "localhost", port, "/categoria-activo", tokenVictoria, "idCuenta", idCuentaValues);
            var respuesta = testRestTemplate.exchange(peticion,
                    new ParameterizedTypeReference<List<CategoriaDTO>>() {});
            assertThat(respuesta.getStatusCode().value()).isEqualTo(403);
            assertThat(respuesta.hasBody()).isEqualTo(false);
        }
    }
}
