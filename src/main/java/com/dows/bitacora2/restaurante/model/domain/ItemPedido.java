package com.dows.bitacora2.restaurante.model.domain;

import java.util.List;

public class ItemPedido {
    private Long idPlato;
    private String nombrePlato;
    private Double precioCongelado;
    private Integer cantidad;
    private String masa;
    private String salsa;
    private List<String> toppings;

    public ItemPedido() {}

    public ItemPedido(Long idPlato, String nombrePlato, Double precioCongelado, Integer cantidad, String masa, String salsa, List<String> toppings) {
        this.idPlato = idPlato;
        this.nombrePlato = nombrePlato;
        this.precioCongelado = precioCongelado;
        this.cantidad = cantidad;
        this.masa = masa;
        this.salsa = salsa;
        this.toppings = toppings;
    }

    public Double subtotal() {
        return (this.precioCongelado != null ? this.precioCongelado : 0.0) * (this.cantidad != null ? this.cantidad : 0);
    }

    public Long getIdPlato() { return idPlato; }
    public void setIdPlato(Long idPlato) { this.idPlato = idPlato; }
    public String getNombrePlato() { return nombrePlato; }
    public void setNombrePlato(String nombrePlato) { this.nombrePlato = nombrePlato; }
    public Double getPrecioCongelado() { return precioCongelado; }
    public void setPrecioCongelado(Double precioCongelado) { this.precioCongelado = precioCongelado; }
    public Integer getCantidad() { return cantidad; }
    public void setCantidad(Integer cantidad) { this.cantidad = cantidad; }
    public String getMasa() { return masa; }
    public void setMasa(String masa) { this.masa = masa; }
    public String getSalsa() { return salsa; }
    public void setSalsa(String salsa) { this.salsa = salsa; }
    public List<String> getToppings() { return toppings; }
    public void setToppings(List<String> toppings) { this.toppings = toppings; }
}
