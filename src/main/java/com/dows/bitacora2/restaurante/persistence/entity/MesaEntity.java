package com.dows.bitacora2.restaurante.persistence.entity;

import com.dows.bitacora2.restaurante.model.domain.EstadoMesa;
import jakarta.persistence.*;

@Entity
@Table(name = "mesas")
public class MesaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private Integer numero;

    @Column(nullable = false)
    private Integer capacidad;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoMesa estado;

    @Column(nullable = false)
    private Boolean cuentaAbierta;

    public MesaEntity() {}

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
