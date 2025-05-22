package com.miniinfomates2003.asset_management;

import com.miniinfomates2003.asset_management.controllers.Mapper;
import com.miniinfomates2003.asset_management.dtos.ActivoDTO;
import com.miniinfomates2003.asset_management.dtos.CategoriaDTO;
import com.miniinfomates2003.asset_management.entities.Activo;
import com.miniinfomates2003.asset_management.entities.Categoria;
import com.miniinfomates2003.asset_management.entities.Usuario;
import com.miniinfomates2003.asset_management.exceptions.NoAccessException;
import com.miniinfomates2003.asset_management.exceptions.NotFoundException;
import com.miniinfomates2003.asset_management.repositories.ActivoRepository;
import com.miniinfomates2003.asset_management.repositories.CategoriaRepository;
import com.miniinfomates2003.asset_management.security.SecurityConfguration;
import com.miniinfomates2003.asset_management.services.ActivoService;
import com.miniinfomates2003.asset_management.services.CuentaService;
import com.miniinfomates2003.asset_management.services.CuentaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Nested;
import org.mockito.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
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
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
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

    @Nested
    @DisplayName("en el controlador de activo")
    class ActivoControllerTests {

        @Nested
        @DisplayName("al modificar un activo")
        class PutActivoTests {

            @Test
            @DisplayName("devuelve OK 200 al modificar un activo existente correctamente")
            void testPutActivoExistenteDevuelve200() {
                // Arrange
                var activo = new ActivoDTO();
                activo.setNombre("Activo 1");
                activo.setTipo("tipo1");
                activo.setTamanio(100);
                activo.setUrl("http://url1.com");
                activo.setCategorias(new ArrayList<>());
                activo.setProductos(List.of(1));

                var peticionCrear = postWithQueryParams("http", "localhost", port, "/activo", tokenAdmin,
                        activo,
                        "idCuenta",
                        List.of(1L)
                );

                // Act
                var respuestaCrear = testRestTemplate.exchange(peticionCrear, ActivoDTO.class);

                // Assert
                assertThat(respuestaCrear.getStatusCode().value()).isEqualTo(201);

                // Arrange (modificación)
                var activoCreado = respuestaCrear.getBody();
                assertThat(activoCreado).isNotNull();
                Integer idActivo = activoCreado.getId();

                var activoModificado = new ActivoDTO();
                activoModificado.setNombre("Activo Modificado");
                activoModificado.setTipo("tipo2");
                activoModificado.setTamanio(200);
                activoModificado.setUrl("http://url2.com");
                activoModificado.setCategorias(new ArrayList<>());
                activoModificado.setProductos(List.of(2));

                var peticionModificar = putWithQueryParams("http", "localhost", port, "/activo/" + idActivo,
                        tokenAdmin,
                        activoModificado,
                        "idCuenta",
                        List.of(1L)
                );

                // Act
                var respuestaModificar = testRestTemplate.exchange(peticionModificar, ActivoDTO.class);

                // Assert
                assertThat(respuestaModificar.getStatusCode().value()).isEqualTo(200);
            }

            @Test
            @DisplayName("devuelve error 404 al intentar modificar un activo no existente")
            void testPutActivoNoExistenteDevuelve404() {
                // Arrange: crear un ActivoDTO para la petición de modificación
                var activoModificado = new ActivoDTO();
                activoModificado.setNombre("Activo Modificado");
                activoModificado.setTipo("tipo2");
                activoModificado.setTamanio(200);
                activoModificado.setUrl("http://url2.com");
                activoModificado.setCategorias(new ArrayList<>());
                activoModificado.setProductos(List.of(2));

                Integer idNoExistente = 9999;  // Asegúrate que no exista en BD

                // Construir la petición PUT con token
                var peticionModificar = putWithQueryParams("http", "localhost", port, "/activo/" + idNoExistente,
                        tokenAdmin,
                        activoModificado,
                        "idCuenta",
                        List.of(1L)
                );

                // Act
                var respuestaModificar = testRestTemplate.exchange(peticionModificar, ActivoDTO.class);

                // Assert
                assertThat(respuestaModificar.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
            }

            @Test
            @DisplayName("devuelve error 403 al intentar modificar un activo sin permisos")
            void testPutActivoSinPermisosDevuelve403() {
                // Arrange: crear el activo como admin
                var activo = new ActivoDTO();
                activo.setNombre("Activo 1");
                activo.setTipo("tipo1");
                activo.setTamanio(100);
                activo.setUrl("http://url1.com");
                activo.setCategorias(new ArrayList<>());
                activo.setProductos(List.of(1));

                var peticionCrear = postWithQueryParams("http", "localhost", port, "/activo", tokenAdmin,
                        activo,
                        "idCuenta",
                        List.of(3L)
                );

                var respuestaCrear = testRestTemplate.exchange(peticionCrear, ActivoDTO.class);
                assertThat(respuestaCrear.getStatusCode().value()).isEqualTo(201);
                var activoCreado = respuestaCrear.getBody();
                assertThat(activoCreado).isNotNull();
                Integer idActivo = activoCreado.getId();

                // Arrange: intento de modificación con token sin permisos
                var activoModificado = new ActivoDTO();
                activoModificado.setNombre("Intento de modificación");
                activoModificado.setTipo("tipo2");
                activoModificado.setTamanio(200);
                activoModificado.setUrl("http://url2.com");
                activoModificado.setCategorias(new ArrayList<>());
                activoModificado.setProductos(List.of(2));

                var peticionModificar = putWithQueryParams("http", "localhost", port, "/activo/" + idActivo,
                        tokenVictoria,
                        activoModificado,
                        "idCuenta",
                        List.of(3L)
                );

                // Act
                var respuestaModificar = testRestTemplate.exchange(peticionModificar, ActivoDTO.class);

                // Assert
                assertThat(respuestaModificar.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
            }

            @Test
            @DisplayName("devuelve error 500 al modificar un activo con campos nulos")
            void testPutActivoConErrorInternoDevuelve500() {
                // Arrange: crear el activo como admin
                var activo = new ActivoDTO();
                activo.setNombre("Activo 1");
                activo.setTipo("tipo1");
                activo.setTamanio(100);
                activo.setUrl("http://url1.com");
                activo.setCategorias(new ArrayList<>());
                activo.setProductos(List.of(1));

                var peticionCrear = postWithQueryParams("http", "localhost", port, "/activo", tokenAdmin,
                        activo,
                        "idCuenta",
                        List.of(1L)
                );

                var respuestaCrear = testRestTemplate.exchange(peticionCrear, ActivoDTO.class);
                assertThat(respuestaCrear.getStatusCode().value()).isEqualTo(201);
                var activoCreado = respuestaCrear.getBody();
                assertThat(activoCreado).isNotNull();
                Integer idActivo = activoCreado.getId();

                // Arrange: modificar el activo pero con un campo nulo que cause error
                var activoModificado = new ActivoDTO();
                activoModificado.setNombre(null); // si tu servicio no lo gestiona, puede fallar
                activoModificado.setTipo(null);   // puedes forzar que peten ciertas validaciones
                activoModificado.setProductos(null); // suponiendo que hace un stream sobre esto

                var peticionModificar = putWithQueryParams("http", "localhost", port, "/activo/" + idActivo,
                        tokenAdmin,
                        activoModificado,
                        "idCuenta",
                        List.of(1L)
                );

                // Act
                var respuestaModificar = testRestTemplate.exchange(peticionModificar, ActivoDTO.class);

                // Assert
                assertThat(respuestaModificar.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }

        @Nested
        @DisplayName("al eliminar un activo")
        class DeleteActivoTests {

            @Test
            @DisplayName("devuelve error 200 al eliminar un activo existente correctamente")
            void testDeleteActivoExistenteDevuelve200() {
                // Arrange: crear el activo como admin
                var activo = new ActivoDTO();
                activo.setNombre("Activo 1");
                activo.setTipo("tipo1");
                activo.setTamanio(100);
                activo.setUrl("http://url1.com");
                activo.setCategorias(new ArrayList<>());
                activo.setProductos(List.of(1));

                var peticionCrear = postWithQueryParams("http", "localhost", port, "/activo", tokenAdmin,
                        activo,
                        "idCuenta",
                        List.of(1L)
                );

                var respuestaCrear = testRestTemplate.exchange(peticionCrear, ActivoDTO.class);
                assertThat(respuestaCrear.getStatusCode().value()).isEqualTo(201);
                var activoCreado = respuestaCrear.getBody();
                assertThat(activoCreado).isNotNull();
                Integer idActivo = activoCreado.getId();

                // Act: eliminar el activo
                var peticionEliminar = delete("http", "localhost", port, "/activo/" + idActivo, tokenAdmin);

                var respuestaEliminar = testRestTemplate.exchange(peticionEliminar, Void.class);

                // Assert
                assertThat(respuestaEliminar.getStatusCode()).isEqualTo(HttpStatus.OK);
            }

            @Test
            @DisplayName("devuelve error 404 al intentar eliminar un activo que no existe")
            void testEliminarActivoInexistenteDevuelve404() {
                // Arrange: definir un ID de activo que no existe
                Integer idActivoInexistente = 99999;

                var peticionEliminar = delete("http", "localhost", port, "/activo/" + idActivoInexistente, tokenAdmin);

                // Act
                var respuestaEliminar = testRestTemplate.exchange(peticionEliminar, Void.class);

                // Assert
                assertThat(respuestaEliminar.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
            }

            @Test
            @DisplayName("devuelve error 403 cuando un usuario intenta borrar un activo sobre el que no tiene permisos")
            void testEliminarActivoSinPermisosDevuelve403() {
                // Arrange: crear el activo como admin para tener algo que borrar
                var activo = new ActivoDTO();
                activo.setNombre("Activo Sin Permisos");
                activo.setTipo("tipo1");
                activo.setTamanio(50);
                activo.setUrl("http://url-sin-permisos.com");
                activo.setCategorias(new ArrayList<>());
                activo.setProductos(List.of(1));

                var peticionCrear = postWithQueryParams("http", "localhost", port, "/activo", tokenAntonio,
                        activo,
                        "idCuenta",
                        List.of(3L)
                );

                var respuestaCrear = testRestTemplate.exchange(peticionCrear, ActivoDTO.class);
                assertThat(respuestaCrear.getStatusCode().value()).isEqualTo(201);
                var activoCreado = respuestaCrear.getBody();
                assertThat(activoCreado).isNotNull();
                Integer idActivo = activoCreado.getId();

                // Act: intentar eliminar con un token de usuario sin permisos
                var peticionEliminar = delete("http", "localhost", port, "/activo/" + idActivo, tokenVictoria);

                var respuestaEliminar = testRestTemplate.exchange(peticionEliminar, Void.class);

                // Assert
                assertThat(respuestaEliminar.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
            }
        }
    }

    @Nested
    @DisplayName("en el servicio de activo")
    class ActivoServiceTest {

        @Mock
        private ActivoRepository activoRepository;

        @Mock
        private CategoriaRepository categoriaRepository;

        @InjectMocks
        private ActivoService activoService; // Aquí sí queremos el real con mocks de dependencias

        @Nested
        @DisplayName("al actualizar un activo")
        class UpdateActivoServiceTests {
            // Con este test no cambia nada en jacoco
            @Test
            @DisplayName("no procesa categorías si el DTO tiene una lista vacía y el activo tiene categorías en null")
            void updateActivo_categoriasNull_noProcesaCategorias() {
                Integer id = 1;
                ActivoDTO dto = new ActivoDTO();
                dto.setCategorias(Collections.emptyList());
                dto.setProductos(Collections.emptyList());  // <-- Aquí

                Activo activo = new Activo();
                activo.setCategorias(null);

                when(activoRepository.findById(id)).thenReturn(Optional.of(activo));

                ActivoService spyService = Mockito.spy(activoService);
                doReturn(true).when(spyService).hasPermissionToUpdate(id);

                Activo resultado = spyService.updateActivo(id, dto);

                assertNotNull(resultado);
                verify(categoriaRepository, never()).save(any());
            }

            @Test
            @DisplayName("crea un nuevo conjunto de activos y guarda si la categoría tiene activos en null")
            void updateActivo_categoriaConActivosNull_creaSetYGuarda() {
                // Arrange
                Integer id = 2;
                Categoria cat = new Categoria();
                cat.setId(10);
                cat.setActivos(null);  // rama a cubrir

                Activo activo = new Activo();
                activo.setCategorias(Set.of(cat));

                ActivoDTO dto = new ActivoDTO();
                dto.setCategorias(List.of(Mapper.toDTO(cat)));
                dto.setProductos(Collections.emptyList()); // <-- importante para evitar NPE

                when(activoRepository.findById(id)).thenReturn(Optional.of(activo));
                when(categoriaRepository.findById(cat.getId())).thenReturn(Optional.of(cat));

                // Crea un spy del servicio real
                ActivoService spyService = Mockito.spy(activoService);
                doReturn(true).when(spyService).hasPermissionToUpdate(id);

                // Act
                spyService.updateActivo(id, dto);

                // Assert
                assertNotNull(cat.getActivos());
                verify(categoriaRepository).save(cat);
            }

            @Test
            @DisplayName("agrega un activo al conjunto existente y guarda si la categoría ya tiene activos")
            void updateActivo_categoriaConActivosNoNull_agregaYGuarda() {
                // Arrange
                Integer id = 3;

                Categoria cat = new Categoria();
                cat.setId(20);
                cat.setActivos(new HashSet<>()); // No null

                Activo activo = new Activo();
                activo.setCategorias(Set.of(cat));

                ActivoDTO dto = new ActivoDTO();
                dto.setCategorias(List.of(Mapper.toDTO(cat)));
                dto.setProductos(Collections.emptyList()); // Para evitar null pointer en productos

                when(activoRepository.findById(id)).thenReturn(Optional.of(activo));
                when(categoriaRepository.findById(cat.getId())).thenReturn(Optional.of(cat));

                // Crea un spy del servicio real
                ActivoService spyService = Mockito.spy(activoService);
                doReturn(true).when(spyService).hasPermissionToUpdate(id);

                // Act
                spyService.updateActivo(id, dto);

                // Assert
                assertTrue(cat.getActivos().contains(activo));
                verify(categoriaRepository).save(cat);
            }
        }

        @Nested
        @DisplayName("al eliminar un activo")
        class DeleteActivoServiceTests {

            @Test
            @DisplayName("elimina un activo con categorías nulas no procesa categorías")
            void eliminarActivo_categoriasNulas_noProcesaCategorias() {
                // Arrange
                Integer id = 1;
                Activo activo = new Activo();
                activo.setCategorias(null); // Cubre rama de categorías nulas

                when(activoRepository.findById(id)).thenReturn(Optional.of(activo));

                ActivoService spyService = Mockito.spy(activoService);
                doReturn(true).when(spyService).hasPermissionToUpdate(id);

                // Act
                spyService.deleteActivo(id);

                // Assert
                verify(categoriaRepository, never()).findById(any());
                verify(categoriaRepository, never()).save(any());
                verify(activoRepository).delete(activo);
            }

            @Test
            @DisplayName("elimina un activo con categorías vacías no procesa categorías")
            void eliminarActivo_categoriasVacias_noProcesaCategorias() {
                // Arrange
                Integer id = 2;
                Activo activo = new Activo();
                activo.setCategorias(Collections.emptySet()); // Cubre rama de categorías vacías

                when(activoRepository.findById(id)).thenReturn(Optional.of(activo));

                ActivoService spyService = Mockito.spy(activoService);
                doReturn(true).when(spyService).hasPermissionToUpdate(id);

                // Act
                spyService.deleteActivo(id);

                // Assert
                verify(categoriaRepository, never()).findById(any());
                verify(categoriaRepository, never()).save(any());
                verify(activoRepository).delete(activo);
            }

            @Test
            @DisplayName("elimina un activo con categorías no nulas y activos nulos en categoría")
            void eliminarActivo_categoriasNoNulas_activosNulos() {
                // Arrange
                Integer id = 3;
                Categoria categoria = new Categoria();
                categoria.setId(10);
                categoria.setActivos(null); // Cubre rama de activos nulos

                Activo activo = new Activo();
                activo.setCategorias(Set.of(categoria));

                when(activoRepository.findById(id)).thenReturn(Optional.of(activo));
                when(categoriaRepository.findById(categoria.getId())).thenReturn(Optional.of(categoria));

                ActivoService spyService = Mockito.spy(activoService);
                doReturn(true).when(spyService).hasPermissionToUpdate(id);

                // Act
                spyService.deleteActivo(id);

                // Assert
                verify(categoriaRepository).findById(categoria.getId());
                verify(categoriaRepository, never()).save(any()); // No se debe llamar a save
                verify(activoRepository).delete(activo);
                assertNull(categoria.getActivos()); // Activos sigue siendo null
            }

            @Test
            @DisplayName("elimina un activo con categorías no nulas y activos no nulos en categoría")
            void eliminarActivo_categoriasNoNulas_activosNoNulos() {
                // Arrange
                Integer id = 4;
                Categoria categoria = new Categoria();
                categoria.setId(20);
                categoria.setActivos(new HashSet<>()); // Activos no nulos
                categoria.getActivos().add(new Activo()); // Agregar otro activo para conjunto no vacío

                Activo activo = new Activo();
                activo.setCategorias(Set.of(categoria));
                categoria.getActivos().add(activo); // Asegurar que el activo está en la categoría

                when(activoRepository.findById(id)).thenReturn(Optional.of(activo));
                when(categoriaRepository.findById(categoria.getId())).thenReturn(Optional.of(categoria));

                ActivoService spyService = Mockito.spy(activoService);
                doReturn(true).when(spyService).hasPermissionToUpdate(id);

                // Act
                spyService.deleteActivo(id);

                // Assert
                verify(categoriaRepository).findById(categoria.getId());
                verify(categoriaRepository).save(categoria);
                verify(activoRepository).delete(activo);
                assertFalse(categoria.getActivos().contains(activo)); // El activo debe ser eliminado
            }

            @Test
            @DisplayName("al eliminar un activo inexistente lanza NotFoundException")
            void eliminarActivo_activoInexistente_lanzaNotFoundException() {
                // Arrange
                Integer id = 5;
                when(activoRepository.findById(id)).thenReturn(Optional.empty());

                // Act & Assert
                assertThrows(NotFoundException.class, () -> activoService.deleteActivo(id));
                verify(activoRepository, never()).delete(any());
                verify(categoriaRepository, never()).findById(any());
            }

            @Test
            @DisplayName("al eliminar un activo sin permisos lanza NoAccessException")
            void eliminarActivo_sinPermisos_lanzaNoAccessException() {
                // Arrange
                Integer id = 6;
                Activo activo = new Activo();
                when(activoRepository.findById(id)).thenReturn(Optional.of(activo));

                ActivoService spyService = Mockito.spy(activoService);
                doReturn(false).when(spyService).hasPermissionToUpdate(id);

                // Act & Assert
                assertThrows(NoAccessException.class, () -> spyService.deleteActivo(id));
                verify(activoRepository, never()).delete(any());
                verify(categoriaRepository, never()).findById(any());
            }
        }

    }

    @Nested
    @DisplayName("en el servicio de activo el metodo hasPermissionToUpdate")
    class ActivoServiceHasPermissionToUpdateTest {

        @Mock
        private ActivoRepository activoRepository;

        @Mock
        private CuentaService cuentaService;

        @Spy
        @InjectMocks
        private ActivoService activoService;

        @Test
        @DisplayName("devuelve true cuando el usuario autenticado está en la lista de usuarios asociados a la cuenta")
        void hasPermissionToUpdate_usuarioAsociado_devuelveTrue() {
            // Arrange
            Integer idActivo = 1;

            // Usuario autenticado (como UserDetails)
            UserDetails usuarioAutenticado = User.withUsername("123")
                    .password("dummy")
                    .roles("USER")
                    .build();

            // Activo simulado
            Activo activo = new Activo();
            activo.setIdCuenta(10);

            // Usuario asociado
            Usuario usuarioAsociado = new Usuario();
            usuarioAsociado.setId(123L);

            try (MockedStatic<SecurityConfguration> mockedSecurity = Mockito.mockStatic(SecurityConfguration.class)) {
                mockedSecurity.when(SecurityConfguration::getAuthenticatedUser)
                        .thenReturn(Optional.of(usuarioAutenticado));

                when(activoRepository.findById(idActivo)).thenReturn(Optional.of(activo));
                when(cuentaService.getUsuariosAsociadosACuenta(activo.getIdCuenta()))
                        .thenReturn(Optional.of(List.of(usuarioAsociado)));

                doReturn(false).when(activoService).isAdmin(usuarioAutenticado);

                // Act
                boolean result = activoService.hasPermissionToUpdate(idActivo);

                // Assert
                assertTrue(result);
            }
        }
    }
}
