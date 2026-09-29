package com.dows.bitacora2.restaurante.model.dto.response;

import com.dows.bitacora2.restaurante.model.domain.EstadoMesa;

public class MesaResponseDTO {

    private Long id;
    private Integer numero;
    private Integer capacidad;
    private EstadoMesa estado;
    private Boolean cuentaAbierta;

    public MesaResponseDTO() {}

    public MesaResponseDTO(Long id, Integer numero, Integer capacidad, EstadoMesa estado, Boolean cuentaAbierta) {
        this.id = id;
        this.numero = numero;
        this.capacidad = capacidad;
        this.estado = estado;
        this.cuentaAbierta = cuentaAbierta;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Integer getNumero() { return numero; }
    public void setNumero(Integer numero) { this.numero = numero; }
    public Integer getCapacidad() { return capacidad; }
    public void setCapacidad(Integer capacidad) { this.capacidad = capacidad; }
    public EstadoMesa getEstado() { return estado; }
    public void setEstado(EstadoMesa estado) { this.estado = estado; }
    public Boolean getCuentaAbierta() { return cuentaAbierta; }
    public void setCuentaAbierta(Boolean cuentaAbierta) { this.cuentaAbierta = cuentaAbierta; }
}
