package com.dows.bitacora2.restaurante.controller;

import com.dows.bitacora2.restaurante.exception.ReglaDeNegocioException;
import com.dows.bitacora2.restaurante.mapper.CuentaMapper;
import com.dows.bitacora2.restaurante.service.ICuentaService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = CuentaController.class)
@AutoConfigureMockMvc(addFilters = false) // Disable security filters to isolate controller logic
class CuentaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ICuentaService cuentaService;

    @MockBean
    private CuentaMapper cuentaMapper;

    @MockBean
    private com.dows.bitacora2.restaurante.security.JwtAuthFilter jwtAuthFilter;

    @MockBean
    private com.dows.bitacora2.restaurante.security.JwtUtil jwtUtil;

    @Test
    void pagar_ConPedidosPendientes_DevuelveError() throws Exception {
        // Arrange
        when(cuentaService.pagar(anyLong())).thenThrow(new ReglaDeNegocioException("No se puede pagar la cuenta. Existen pedidos pendientes en la mesa."));

        // Act & Assert
        mockMvc.perform(patch("/api/v1/cuentas/1/pago")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnprocessableEntity());
    }
}
