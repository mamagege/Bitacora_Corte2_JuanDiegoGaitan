package edu.dosw.parcial.persistence.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

/**
 * Entidad de persistencia documental para colección MongoDB 'barberos'.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "barberos")
public class BarberDocument {

    @Id
    private String id;

    @Indexed(unique = true)
    private String name;

    private List<String> specialties;
    private List<String> days;
    private int startHour;
    private int endHour;
}
