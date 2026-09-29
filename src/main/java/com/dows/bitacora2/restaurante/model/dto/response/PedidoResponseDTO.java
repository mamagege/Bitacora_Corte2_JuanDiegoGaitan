package com.dows.bitacora2.restaurante.model.dto.response;

import java.time.LocalDateTime;
import java.util.List;

public class PedidoResponseDTO {

    private Long id;
    private Long idMesa;
    private List<ItemPedidoResponseDTO> items;
    private String estado;
    private LocalDateTime timestamp;
    private Double totalPedido;

    public PedidoResponseDTO() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getIdMesa() { return idMesa; }
    public void setIdMesa(Long idMesa) { this.idMesa = idMesa; }
    public List<ItemPedidoResponseDTO> getItems() { return items; }
    public void setItems(List<ItemPedidoResponseDTO> items) { this.items = items; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
    public Double getTotalPedido() { return totalPedido; }
    public void setTotalPedido(Double totalPedido) { this.totalPedido = totalPedido; }
}
