package com.dows.bitacora2.restaurante.model.domain;

import java.time.LocalDateTime;
import java.util.List;

public class Pedido {
    private Long id;
    private Long idMesa;
    private List<ItemPedido> items;
    private EstadoPedido estado;
    private LocalDateTime timestamp;

    public Pedido() {}

    public Pedido(Long id, Long idMesa, List<ItemPedido> items, EstadoPedido estado, LocalDateTime timestamp) {
        this.id = id;
        this.idMesa = idMesa;
        this.items = items;
        this.estado = estado;
        this.timestamp = timestamp;
    }

    public boolean puedeModificarse() {
        return this.estado == EstadoPedido.RECIBIDO;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getIdMesa() { return idMesa; }
    public void setIdMesa(Long idMesa) { this.idMesa = idMesa; }
    public List<ItemPedido> getItems() { return items; }
    public void setItems(List<ItemPedido> items) { this.items = items; }
    public EstadoPedido getEstado() { return estado; }
    public void setEstado(EstadoPedido estado) { this.estado = estado; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
}
