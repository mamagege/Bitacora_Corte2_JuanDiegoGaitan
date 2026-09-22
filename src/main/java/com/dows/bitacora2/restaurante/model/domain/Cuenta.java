package com.dows.bitacora2.restaurante.model.domain;

import java.time.LocalDateTime;

public class Cuenta {
    private Long id;
    private Long idMesa;
    private Double total;
    private EstadoCuenta estado;
    private LocalDateTime fechaApertura;

    public Cuenta() {}

    public Cuenta(Long id, Long idMesa, Double total, EstadoCuenta estado, LocalDateTime fechaApertura) {
        this.id = id;
        this.idMesa = idMesa;
        this.total = total;
        this.estado = estado;
        this.fechaApertura = fechaApertura;
    }

    public Double calcularTotal() {
        return this.total != null ? this.total : 0.0;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getIdMesa() { return idMesa; }
    public void setIdMesa(Long idMesa) { this.idMesa = idMesa; }
    public Double getTotal() { return total; }
    public void setTotal(Double total) { this.total = total; }
    public EstadoCuenta getEstado() { return estado; }
    public void setEstado(EstadoCuenta estado) { this.estado = estado; }
    public LocalDateTime getFechaApertura() { return fechaApertura; }
    public void setFechaApertura(LocalDateTime fechaApertura) { this.fechaApertura = fechaApertura; }
}
