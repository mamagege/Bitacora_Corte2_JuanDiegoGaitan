package com.dows.bitacora2.restaurante.persistence.document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.Map;

@Document(collection = "eventos_restaurante")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventoRestauranteDocument {

    @Id
    private String id;  

    private String        tipo;         
    private String        entidadTipo;  
    private Long          entidadId;
    private String        descripcion;
    private String        usuario;
    private LocalDateTime timestamp;

    private Map<String, Object> metadatos;
}
