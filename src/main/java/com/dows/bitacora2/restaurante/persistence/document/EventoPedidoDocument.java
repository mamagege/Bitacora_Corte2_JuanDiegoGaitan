package com.dows.bitacora2.restaurante.persistence.document;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;

@Document(collection = "eventos_pedido")
public class EventoPedidoDocument {

    @Id
    private String id; // MongoDB ObjectId

    private Long pedidoId;
    
    private String estadoAnterior;
    
    private String estadoNuevo;
    
    private LocalDateTime fechaEvento;
    
    private String descripcion;

    public EventoPedidoDocument() {}

    public EventoPedidoDocument(Long pedidoId, String estadoAnterior, String estadoNuevo, LocalDateTime fechaEvento, String descripcion) {
        this.pedidoId = pedidoId;
        this.estadoAnterior = estadoAnterior;
        this.estadoNuevo = estadoNuevo;
        this.fechaEvento = fechaEvento;
        this.descripcion = descripcion;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public Long getPedidoId() { return pedidoId; }
    public void setPedidoId(Long pedidoId) { this.pedidoId = pedidoId; }
    public String getEstadoAnterior() { return estadoAnterior; }
    public void setEstadoAnterior(String estadoAnterior) { this.estadoAnterior = estadoAnterior; }
    public String getEstadoNuevo() { return estadoNuevo; }
    public void setEstadoNuevo(String estadoNuevo) { this.estadoNuevo = estadoNuevo; }
    public LocalDateTime getFechaEvento() { return fechaEvento; }
    public void setFechaEvento(LocalDateTime fechaEvento) { this.fechaEvento = fechaEvento; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
}
