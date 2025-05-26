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
import org.springframework.http.*;

import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;
import org.springframework.web.util.UriBuilder;
import org.springframework.web.util.UriBuilderFactory;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

import static org.hamcrest.Matchers.matchesPattern;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;

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

    @Value(value = "${tokenAntonio}")
    private String tokenAntonio;

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

    private RequestEntity<Void> delete(String scheme, String host, int port, String path, String token) {
        URI uri = uri(scheme, host, port, path);
        var peticion = RequestEntity.delete(uri)
                .header("Authorization", "Bearer " + token)
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

    private <T> RequestEntity<T> putWithQueryParams(String scheme, String host, int port, String path, String token, T body, String paramName, List<Long> paramValues) {
        URI uri = uriWithQueryParams(scheme, host, port, path, paramName, paramValues);
        var peticion = RequestEntity.put(uri)
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + token)
                .body(body);
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

    public void simulaRespuestaUsuariosCuentaDos() {
        var uriRemota = UriComponentsBuilder.fromUriString(baseURL + "/cuenta/2/usuarios")
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

    private void simulaRespuestaMaxNumActivosCuentaTres() {
        var uriRemota = UriComponentsBuilder.fromUriString(baseURL + "/cuenta")
                .queryParam("idCuenta", 3)
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
                                    "id": 3,
                                    "nombre": "Cuenta 3",
                                    "direccion": "Calle Ficticia 456, Ciudad Ficticia",
                                    "nif": "87654321A",
                                    "fechaAlta": "2023-02-15",
                                    "plan": {
                                      "id": 2,
                                      "nombre": "Plan Avanzado",
                                      "maxProductos": 10,
                                      "maxActivos": 10,
                                      "maxAlmacenamiento": 10,
                                      "maxCategoriasProductos": 5,
                                      "maxCategoriasActivos": 5,
                                      "maxRelaciones": 2,
                                      "precio": 19.99
                                    }
                                  }
                                ]
                                """
                        ));
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
        @DisplayName("devuelve error 404 al intentar modificar un activo no existente")
        void testPutActivoNoExistenteDevuelve404() {
            // Arrange
            var activoModificado = new ActivoDTO();
            activoModificado.setNombre("Activo Modificado");
            activoModificado.setTipo("tipo2");
            activoModificado.setTamanio(200);
            activoModificado.setUrl("http://url2.com");
            activoModificado.setCategorias(new ArrayList<>());
            activoModificado.setProductos(List.of(2));

            Integer idNoExistente = 9999;

            var peticionModificar = putWithQueryParams("http", "localhost", port, "/activo/" + idNoExistente,
                    tokenAdmin, activoModificado, "idCuenta", List.of(1L));

            // Act
            var respuestaModificar = testRestTemplate.exchange(peticionModificar, ActivoDTO.class);

            // Assert
            assertThat(respuestaModificar.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
            assertThat(respuestaModificar.getBody()).isNull();
        }

        @Test
        @DisplayName("devuelve error 404 al intentar eliminar un activo que no existe")
        void testEliminarActivoInexistenteDevuelve404() {
            // Arrange
            Integer idActivoInexistente = 99999;

            var peticionEliminar = delete("http", "localhost", port, "/activo/" + idActivoInexistente, tokenAdmin);

            // Act
            var respuestaEliminar = testRestTemplate.exchange(peticionEliminar, Void.class);

            // Assert
            assertThat(respuestaEliminar.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
            assertThat(respuestaEliminar.getBody()).isNull();
        }
    }

    @Nested
    @DisplayName("cuando hay activos")
    class HayActivos {

        private Activo activoCuenta1;
        private Activo activoCuenta2;
        private Activo activoCuenta3;

        @BeforeEach
        public void introduceDatos() {
            activoCuenta1 = Activo.builder()
                    .nombre("Manual del televisor")
                    .tipo("PDF")
                    .tamanio(2)
                    .url("https://mallba3.lcc.uma.es/activos/manual-televisor.pdf")
                    .idCuenta(1)
                    .build();

            activoRepository.save(activoCuenta1);

            activoCuenta2 = Activo.builder()
                    .nombre("Manual del televisor")
                    .tipo("PDF")
                    .tamanio(2)
                    .url("https://mallba3.lcc.uma.es/activos/manual-televisor.pdf")
                    .idCuenta(2)
                    .build();

            activoRepository.save(activoCuenta2);

            Categoria categoria = new Categoria(null, "Categoría Test", 2, Set.of(activoCuenta2));
            categoriaRepository.save(categoria);

            activoCuenta2.setCategorias(Set.of(categoria));
            activoCuenta2.setIdProductos(Set.of(1));
            activoRepository.save(activoCuenta2);

            activoCuenta3 = Activo.builder()
                    .nombre("Manual del televisor")
                    .tipo("PDF")
                    .tamanio(2)
                    .url("https://mallba3.lcc.uma.es/activos/manual-televisor.pdf")
                    .idCuenta(3)
                    .build();

            activoRepository.save(activoCuenta3);
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

        @Test
        @DisplayName("devuelve OK 200 al modificar un activo existente correctamente con el token de admin")
        void testPutActivoExistenteDevuelve200() {
            //Arrange
            simulaRespuestaUsuariosCuentaUno(); // Mock para permisos en PUT

            var activoModificado = new ActivoDTO();
            activoModificado.setNombre("Activo Modificado");
            activoModificado.setTipo("tipo2");
            activoModificado.setTamanio(200);
            activoModificado.setUrl("http://url2.com");
            activoModificado.setCategorias(new ArrayList<>());
            activoModificado.setProductos(List.of(2));

            var peticionModificar = putWithQueryParams("http", "localhost", port, "/activo/" + this.activoCuenta1.getId(),
                    tokenAdmin, activoModificado, "idCuenta", List.of(1L));

            // Act
            var respuestaModificar = testRestTemplate.exchange(peticionModificar, ActivoDTO.class);

            // Assert
            assertThat(respuestaModificar.getStatusCode().value()).isEqualTo(200);
            var activoDevuelto = respuestaModificar.getBody();
            assertThat(activoDevuelto).isNotNull();
            assertThat(activoDevuelto.getNombre()).isEqualTo("Activo Modificado");
            assertThat(activoDevuelto.getTipo()).isEqualTo("tipo2");
            assertThat(activoDevuelto.getTamanio()).isEqualTo(200);
            assertThat(activoDevuelto.getUrl()).isEqualTo("http://url2.com");
            assertThat(activoDevuelto.getProductos()).containsExactly(2);

            // Verificar que los mocks fueron invocados
            mockServer.verify();
        }

        @Test
        @DisplayName("devuelve OK 200 al modificar un activo existente correctamente con un token distinto de admin")
        void testPutActivoExistenteConTokenDistintoDeAdminDevuelve200() {
            // Arrange
            simulaRespuestaUsuariosCuentaUno(); // Mock para permisos en PUT

            var activoModificado = new ActivoDTO();
            activoModificado.setNombre("Activo Modificado");
            activoModificado.setTipo("tipo2");
            activoModificado.setTamanio(200);
            activoModificado.setUrl("http://url2.com");
            activoModificado.setCategorias(new ArrayList<>());
            activoModificado.setProductos(List.of(2));

            var peticionModificar = putWithQueryParams("http", "localhost", port, "/activo/" + this.activoCuenta1.getId(),
                    tokenAntonio, activoModificado, "idCuenta", List.of(1L));

            // Act
            var respuestaModificar = testRestTemplate.exchange(peticionModificar, ActivoDTO.class);

            // Assert
            assertThat(respuestaModificar.getStatusCode().value()).isEqualTo(200);
            var activoDevuelto = respuestaModificar.getBody();
            assertThat(activoDevuelto).isNotNull();
            assertThat(activoDevuelto.getNombre()).isEqualTo("Activo Modificado");
            assertThat(activoDevuelto.getTipo()).isEqualTo("tipo2");
            assertThat(activoDevuelto.getTamanio()).isEqualTo(200);
            assertThat(activoDevuelto.getUrl()).isEqualTo("http://url2.com");
            assertThat(activoDevuelto.getProductos()).containsExactly(2);

            // Verificar que los mocks fueron invocados
            mockServer.verify();
        }

        @Test
        @DisplayName("devuelve OK 200 al modificar un activo con categorías correctamente")
        public void devuelve200AlModificarActivoConCategorias() {
            // Arrange
            simulaRespuestaUsuariosCuentaUno(); // Mock para permisos en PUT

            categoriaRepository.save(new Categoria(null, "Categoría 1", 1, null));
            categoriaRepository.save(new Categoria(null, "Categoría 2", 1, null));
            var categoria1 = new CategoriaDTO(1, "Categoría 1");
            var categoria2 = new CategoriaDTO(2, "Categoría 2");

            var activoModificado = new ActivoDTO();
            activoModificado.setNombre("Activo Modificado");
            activoModificado.setTipo("tipo2");
            activoModificado.setTamanio(200);
            activoModificado.setUrl("http://url2.com");
            activoModificado.setCategorias(List.of(categoria1, categoria2));
            activoModificado.setProductos(List.of(2));

            var peticionModificar = putWithQueryParams("http", "localhost", port, "/activo/" + this.activoCuenta1.getId(),
                    tokenAdmin, activoModificado, "idCuenta", List.of(1L));

            // Act
            var respuestaModificar = testRestTemplate.exchange(peticionModificar, ActivoDTO.class);

            // Assert
            assertThat(respuestaModificar.getStatusCode().value()).isEqualTo(200);
            var activoDevuelto = respuestaModificar.getBody();
            assertThat(activoDevuelto).isNotNull();
            assertThat(activoDevuelto.getCategorias()).hasSize(2);
        }

        @Test
        @DisplayName("devuelve error 403 al intentar modificar un activo sin permisos")
        void testPutActivoSinPermisosDevuelve403() {
            // Arrange
            simulaRespuestaUsuariosCuentaTres(); // Mock para permisos (Victoria no tiene acceso)

            var activoModificado = new ActivoDTO();
            activoModificado.setNombre("Intento de modificación");
            activoModificado.setTipo("tipo2");
            activoModificado.setTamanio(200);
            activoModificado.setUrl("http://url2.com");
            activoModificado.setCategorias(new ArrayList<>());
            activoModificado.setProductos(List.of(2));

            var peticionModificar = putWithQueryParams("http", "localhost", port, "/activo/" + this.activoCuenta3.getId(),
                    tokenVictoria, activoModificado, "idCuenta", List.of(3L));

            // Act
            var respuestaModificar = testRestTemplate.exchange(peticionModificar, ActivoDTO.class);

            // Assert
            assertThat(respuestaModificar.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
            assertThat(respuestaModificar.getBody()).isNull();

            // Verificar que los mocks fueron invocados
            mockServer.verify();
        }

        @Test
        @DisplayName("devuelve error 500 al modificar un activo con campos nulos")
        void testPutActivoConErrorInternoDevuelve500() {
            // Arrange
            simulaRespuestaUsuariosCuentaUno(); // Mock para permisos en PUT

            var activoModificado = new ActivoDTO();
            activoModificado.setNombre(null);
            activoModificado.setTipo(null);
            activoModificado.setProductos(null);

            var peticionModificar = putWithQueryParams("http", "localhost", port, "/activo/" + this.activoCuenta1.getId(),
                    tokenAdmin, activoModificado, "idCuenta", List.of(1L));

            // Act
            var respuestaModificar = testRestTemplate.exchange(peticionModificar, ActivoDTO.class);

            // Assert
            assertThat(respuestaModificar.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
            assertThat(respuestaModificar.getBody()).isNull();

            // Verificar que los mocks fueron invocados
            mockServer.verify();
        }

        @Test
        @DisplayName("devuelve OK 200 al eliminar un activo existente correctamente")
        void testDeleteActivoExistenteDevuelve200() {
            // Arrange
            simulaRespuestaUsuariosCuentaUno(); // Mock para permisos en DELETE
            var peticionEliminar = delete("http", "localhost", port, "/activo/" + this.activoCuenta1.getId(), tokenAdmin);

            // Act
            var respuestaEliminar = testRestTemplate.exchange(peticionEliminar, Void.class);

            // Assert
            assertThat(respuestaEliminar.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(respuestaEliminar.getBody()).isNull();

            // Verificar que los mocks fueron invocados
            mockServer.verify();
        }

        @Test
        @DisplayName("devuelve OK 200 al eliminar un activo existente con categorias correctamente")
        void testDeleteActivoExistenteConCategoriasDevuelve200() {
            // Arrange
            simulaRespuestaUsuariosCuentaDos(); // Mock para permisos en DELETE
            var peticionEliminar = delete("http", "localhost", port, "/activo/" + activoCuenta2.getId(), tokenAdmin);

            // Act
            var respuestaEliminar = testRestTemplate.exchange(peticionEliminar, Void.class);

            // Assert
            assertThat(respuestaEliminar.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(respuestaEliminar.getBody()).isNull();

            // Verificar que los mocks fueron invocados
            mockServer.verify();
        }

        @Test
        @DisplayName("devuelve error 403 cuando un usuario intenta borrar un activo sobre el que no tiene permisos")
        void testEliminarActivoSinPermisosDevuelve403() {
            // Arrange
            simulaRespuestaUsuariosCuentaTres(); // Mock para permisos (DELETE, Victoria no tiene acceso)
            var peticionEliminar = delete("http", "localhost", port, "/activo/" + activoCuenta3.getId(), tokenVictoria);

            // Act
            var respuestaEliminar = testRestTemplate.exchange(peticionEliminar, Void.class);

            // Assert
            assertThat(respuestaEliminar.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
            assertThat(respuestaEliminar.getBody()).isNull();

            // Verificar que los mocks fueron invocados
            mockServer.verify();
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
        @DisplayName("devuelve 400 cuando no se proporciona ningún parámetro en el queryString al obtener una categoría")
        public void obtenerActivosSinParametrosDevuelveBadRequest() {
            // Arrange
            // No necesitamos configurar ningún mock porque el endpoint debería devolver BadRequest
            // antes de llegar a cualquier lógica de servicio

            // Act
            // Realizar petición GET sin parámetros
            var peticion = get("http", "localhost", port, "/categoria-activo", tokenAdmin);
            var respuesta = testRestTemplate.exchange(peticion,
                    new ParameterizedTypeReference<List<CategoriaDTO>>() {});

            // Assert
            assertThat(respuesta.getStatusCode().value()).isEqualTo(400);
            assertThat(respuesta.getBody()).isNull();
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

        @Test
        @DisplayName("se crea correctamente un activo con categorías y productos")
        public void creaActivo() {
            // Arrange
            simulaRespuestaUsuariosCuentaUno();
            simulaRespuestaMaxNumActivosCuentaUno();
            categoriaRepository.save(new Categoria(null, "Manuales", 1, null));

            // Crear set de categorías
            Set<Categoria> categorias = new HashSet<>();
            Categoria categoria1 = Categoria.builder()
                    .id(2)
                    .nombre("Manuales")
                    .idCuenta(1)
                    .build();
            categorias.add(categoria1);

            // Crear set de productos
            Set<Integer> productos = new HashSet<>();
            productos.add(1);
            productos.add(2);

            // Crear el activo con las categorías y productos
            Activo activo = Activo.builder()
                    .nombre("Imagen del ordenador")
                    .tipo("JPG")
                    .tamanio(1)
                    .url("https://mallba3.lcc.uma.es/activos/imagen-ordenador.jpg")
                    .categorias(categorias)
                    .idProductos(productos)
                    .build();

            // Act
            var peticion = postWithQueryParams("http", "localhost", port, "/activo",
                    tokenVictoria, activo, "idCuenta", List.of(1L));
            var respuesta = testRestTemplate.exchange(peticion,
                    new ParameterizedTypeReference<ActivoDTO>() {});

            // Assert
            assertThat(respuesta.getStatusCode().value()).isEqualTo(201);
            assertThat(respuesta.getBody()).isNotNull();
        }
    }

}
