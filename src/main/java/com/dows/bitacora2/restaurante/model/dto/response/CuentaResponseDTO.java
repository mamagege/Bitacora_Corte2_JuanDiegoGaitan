package com.dows.bitacora2.restaurante.model.dto.response;

import java.time.LocalDateTime;

public class CuentaResponseDTO {

    private Long id;
    private Long idMesa;
    private Double total;
    private String estado;
    private LocalDateTime fechaApertura;

    public CuentaResponseDTO() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getIdMesa() { return idMesa; }
    public void setIdMesa(Long idMesa) { this.idMesa = idMesa; }
    public Double getTotal() { return total; }
    public void setTotal(Double total) { this.total = total; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    public LocalDateTime getFechaApertura() { return fechaApertura; }
    public void setFechaApertura(LocalDateTime fechaApertura) { this.fechaApertura = fechaApertura; }
}
