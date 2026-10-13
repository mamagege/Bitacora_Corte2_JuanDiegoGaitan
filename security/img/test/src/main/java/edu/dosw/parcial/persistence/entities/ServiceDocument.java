package edu.dosw.parcial.persistence.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Entidad de persistencia documental para colección MongoDB 'servicios'.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "servicios")
public class ServiceDocument {

    @Id
    private String id;

    @Indexed(unique = true)
    private String code;

    private String name;
    private double price;
    private String specialty;
}
