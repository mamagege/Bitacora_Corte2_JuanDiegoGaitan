package com.dows.bitacora2.restaurante.model.dto.request;

import jakarta.validation.constraints.NotNull;

public class CuentaRequestDTO {

    @NotNull(message = "El id de la mesa es obligatorio")
    private Long idMesa;

    public CuentaRequestDTO() {}

    public CuentaRequestDTO(Long idMesa) {
        this.idMesa = idMesa;
    }

    public Long getIdMesa() { return idMesa; }
    public void setIdMesa(Long idMesa) { this.idMesa = idMesa; }
}
