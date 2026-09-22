package com.dows.bitacora2.restaurante.model.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public class ItemPedidoRequestDTO {

    @NotNull(message = "El id del plato es obligatorio")
    private Long idPlato;

    @NotNull(message = "La cantidad es obligatoria")
    @Min(value = 1, message = "La cantidad debe ser al menos 1")
    private Integer cantidad;

    private String masa;
    private String salsa;
    private List<String> toppings;

    public ItemPedidoRequestDTO() {}

    public ItemPedidoRequestDTO(Long idPlato, Integer cantidad, String masa, String salsa, List<String> toppings) {
        this.idPlato = idPlato;
        this.cantidad = cantidad;
        this.masa = masa;
        this.salsa = salsa;
        this.toppings = toppings;
    }

    public Long getIdPlato() { return idPlato; }
    public void setIdPlato(Long idPlato) { this.idPlato = idPlato; }
    public Integer getCantidad() { return cantidad; }
    public void setCantidad(Integer cantidad) { this.cantidad = cantidad; }
    public String getMasa() { return masa; }
    public void setMasa(String masa) { this.masa = masa; }
    public String getSalsa() { return salsa; }
    public void setSalsa(String salsa) { this.salsa = salsa; }
    public List<String> getToppings() { return toppings; }
    public void setToppings(List<String> toppings) { this.toppings = toppings; }
}
