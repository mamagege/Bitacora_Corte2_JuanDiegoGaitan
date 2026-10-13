package edu.dosw.parcial.controller;

import org.junit.jupiter.api.Test;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class HealthControllerTest {

    @Test
    void testHealthEndpointReturnsUp() {
        HealthController controller = new HealthController();
        Map<String, String> response = controller.health();

        assertNotNull(response);
        assertEquals("UP", response.get("status"));
        assertEquals("DOSW-ParcialT2", response.get("service"));
    }
}
