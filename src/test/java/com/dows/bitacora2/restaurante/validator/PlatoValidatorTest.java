package com.dows.bitacora2.restaurante.validator;

import com.dows.bitacora2.restaurante.exception.ConflictoException;
import com.dows.bitacora2.restaurante.repository.PlatoRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PlatoValidatorTest {

    @Mock
    private PlatoRepository platoRepository;

    @InjectMocks
    private PlatoValidator validator;

    @Test
    @DisplayName("validarNombreUnico - nombre nuevo no lanza excepcion")
    void validarNombreUnico_nombreNuevo_noLanza() {
        when(platoRepository.existsByNombreIgnoreCase("Bandeja Paisa")).thenReturn(false);

        assertDoesNotThrow(() ->
            validator.validarNombreUnico("Bandeja Paisa"));
    }

    @Test
    @DisplayName("validarNombreUnico - nombre duplicado (case-insensitive) lanza ConflictoException")
    void validarNombreUnico_nombreDuplicado_lanzaConflicto() {
        when(platoRepository.existsByNombreIgnoreCase("AJIACO")).thenReturn(true);

        assertThrows(ConflictoException.class, () ->
            validator.validarNombreUnico("AJIACO"));
    }
}
