package com.dows.bitacora2.restaurante.controller;

import com.dows.bitacora2.restaurante.exception.*;
import com.dows.bitacora2.restaurante.model.dto.response.ErrorResponseDTO;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.time.LocalDateTime;
import java.util.stream.Collectors;



@RestControllerAdvice
@lombok.extern.slf4j.Slf4j
public class GlobalExceptionHandler {
    

    @ExceptionHandler(RecursoNoEncontradoException.class)
    public ResponseEntity<ErrorResponseDTO> handleNotFound(RecursoNoEncontradoException ex, HttpServletRequest request) {
        log.warn("RecursoNoEncontrado: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ErrorResponseDTO.of(404, "Not Found", ex.getMessage(), request.getRequestURI()));
    }

    @ExceptionHandler(ConflictoException.class)
    public ResponseEntity<ErrorResponseDTO> handleConflicto(ConflictoException ex, HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ErrorResponseDTO.of(409, "Conflict", ex.getMessage(), request.getRequestURI()));
    }

    @ExceptionHandler({EstadoInvalidoException.class, ReglaDeNegocioException.class})
    public ResponseEntity<ErrorResponseDTO> handleNegocio(RuntimeException ex, HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                .body(ErrorResponseDTO.of(422, "Unprocessable Entity", ex.getMessage(), request.getRequestURI()));
    }

    @ExceptionHandler(BloqueoTemporalException.class)
    public ResponseEntity<ErrorResponseDTO> handleBloqueoTemporal(BloqueoTemporalException ex, HttpServletRequest request) {
        log.warn("Bloqueo temporal: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                .body(ErrorResponseDTO.of(429, "Too Many Requests", ex.getMessage(), request.getRequestURI()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDTO> handleValidacion(MethodArgumentNotValidException ex, HttpServletRequest request) {
        String mensaje = ex.getBindingResult().getFieldErrors().stream()
                .map(e -> e.getField() + ": " + e.getDefaultMessage())
                .collect(Collectors.joining(", "));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ErrorResponseDTO.of(400, "Bad Request", mensaje, request.getRequestURI()));
    }

    @ExceptionHandler(org.springframework.security.access.AccessDeniedException.class)
    public ResponseEntity<ErrorResponseDTO> handleAccessDenied(org.springframework.security.access.AccessDeniedException ex, HttpServletRequest request) {
        log.warn("Acceso denegado (403) a la ruta: {} - Mensaje: {}", request.getRequestURI(), ex.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(ErrorResponseDTO.of(403, "Forbidden", "No tienes permisos para realizar esta acción", request.getRequestURI()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDTO> handleGenerico(Exception ex, HttpServletRequest request) {
        log.error("Error inesperado: {}", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ErrorResponseDTO.of(500, "Internal Server Error", "Error inesperado del servidor", request.getRequestURI()));
    }
}
