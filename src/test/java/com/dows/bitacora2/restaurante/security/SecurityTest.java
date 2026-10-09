package com.dows.bitacora2.restaurante.security;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import com.dows.bitacora2.restaurante.repository.*;
import com.dows.bitacora2.restaurante.service.*;

@WebMvcTest
class SecurityTest {

    @MockBean
    private PlatoRepository platoRepository;
    @MockBean
    private IPlatoService platoService;
    @MockBean
    private PedidoRepository pedidoRepository;
    @MockBean
    private IPedidoService pedidoService;
    @MockBean
    private MesaRepository mesaRepository;
    @MockBean
    private IMesaService mesaService;
    @MockBean
    private CuentaRepository cuentaRepository;
    @MockBean
    private ICuentaService cuentaService;
    @MockBean
    private UsuarioRepository usuarioRepository;
    @MockBean
    private EventoPedidoMongoRepository eventoPedidoMongoRepository;
    @MockBean
    private com.dows.bitacora2.restaurante.security.JwtAuthFilter jwtAuthFilter;
    @MockBean
    private com.dows.bitacora2.restaurante.security.JwtUtil jwtUtil;
    @MockBean
    private IAuthService authService;
    @MockBean
    private com.dows.bitacora2.restaurante.mapper.PlatoMapper platoMapper;
    @MockBean
    private com.dows.bitacora2.restaurante.mapper.PedidoMapper pedidoMapper;
    @MockBean
    private com.dows.bitacora2.restaurante.mapper.MesaMapper mesaMapper;
    @MockBean
    private com.dows.bitacora2.restaurante.mapper.CuentaMapper cuentaMapper;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void crearPlato_sinToken_devuelve403() throws Exception {
        mockMvc.perform(post("/api/v1/platos")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"nombre\":\"Bandeja Paisa\",\"precio\":28000,\"descripcion\":\"Plato tipico\"}"))
                .andExpect(status().isForbidden()); // Espera 403
    }

    @Test
    @WithMockUser(roles = "CLIENTE") // Simula un usuario con rol CLIENTE
    void crearPlato_conRolCliente_devuelve403() throws Exception {
        mockMvc.perform(post("/api/v1/platos")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"nombre\":\"Bandeja Paisa\",\"precio\":28000,\"descripcion\":\"Plato tipico\"}"))
                .andExpect(status().isForbidden()); // Espera 403
    }
}
