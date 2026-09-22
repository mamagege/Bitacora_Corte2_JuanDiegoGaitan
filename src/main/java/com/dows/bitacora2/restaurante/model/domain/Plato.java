package com.dows.bitacora2.restaurante.model.domain;


public class Plato {
    private Long    id;
    private String  nombre;
    private Double  precio;
    private String  categoria;
    private Boolean disponible;
    private String  descripcion;

    public boolean estaDisponible() {
        return Boolean.TRUE.equals(disponible);
    }

    public void activar()   { this.disponible = true;  }
    public void desactivar(){ this.disponible = false; }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public Double getPrecio() { return precio; }
    public void setPrecio(Double precio) { this.precio = precio; }
    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }
    public Boolean getDisponible() { return disponible; }
    public void setDisponible(Boolean disponible) { this.disponible = disponible; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public Plato() {}

    public Plato(Long id, String nombre, Double precio, String categoria, Boolean disponible, String descripcion) {
        this.id = id;
        this.nombre = nombre;
        this.precio = precio;
        this.categoria = categoria;
        this.disponible = disponible;
        this.descripcion = descripcion;
    }
}
