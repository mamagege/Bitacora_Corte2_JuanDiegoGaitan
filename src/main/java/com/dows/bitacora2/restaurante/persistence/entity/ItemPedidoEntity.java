package com.dows.bitacora2.restaurante.persistence.entity;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "item_pedido")
public class ItemPedidoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long idPlato;

    @Column(nullable = false)
    private String nombrePlato;

    @Column(nullable = false)
    private Double precioCongelado;

    @Column(nullable = false)
    private Integer cantidad;

    private String masa;
    
    private String salsa;

    @ElementCollection
    @CollectionTable(name = "item_pedido_toppings", joinColumns = @JoinColumn(name = "item_pedido_id"))
    @Column(name = "topping")
    private List<String> toppings;

    public ItemPedidoEntity() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
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
