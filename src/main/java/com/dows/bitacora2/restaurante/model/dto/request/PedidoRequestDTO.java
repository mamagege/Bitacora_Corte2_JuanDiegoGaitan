package com.dows.bitacora2.restaurante.model.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public class PedidoRequestDTO {

    @NotNull(message = "El id de la mesa es obligatorio")
    private Long idMesa;

    @NotEmpty(message = "El pedido debe tener al menos un item")
    @Valid
    private List<ItemPedidoRequestDTO> items;

    public PedidoRequestDTO() {}

    public PedidoRequestDTO(Long idMesa, List<ItemPedidoRequestDTO> items) {
        this.idMesa = idMesa;
        this.items = items;
    }

    public Long getIdMesa() { return idMesa; }
    public void setIdMesa(Long idMesa) { this.idMesa = idMesa; }
    public List<ItemPedidoRequestDTO> getItems() { return items; }
    public void setItems(List<ItemPedidoRequestDTO> items) { this.items = items; }
}
