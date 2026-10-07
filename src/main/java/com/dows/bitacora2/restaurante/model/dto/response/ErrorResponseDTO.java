package com.dows.bitacora2.restaurante.model.dto.response;
import java.time.LocalDateTime;
public record ErrorResponseDTO(
    LocalDateTime timestamp,
    int status,
    String error,
    String message,
    String path
) {
    public static ErrorResponseDTO of(int status, String error, String message, String path) {
        return new ErrorResponseDTO(LocalDateTime.now(), status, error, message, path);
    }
}