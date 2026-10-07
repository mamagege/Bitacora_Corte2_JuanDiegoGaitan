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

@SpringBootTest
@AutoConfigureMockMvc
class SecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void crearPlato_sinToken_devuelve401() throws Exception {
        mockMvc.perform(post("/api/v1/platos")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"nombre\":\"Bandeja Paisa\",\"precio\":28000,\"descripcion\":\"Plato tipico\"}"))
                .andExpect(status().isUnauthorized()); // Espera 401
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
