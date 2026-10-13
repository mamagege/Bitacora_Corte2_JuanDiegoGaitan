package edu.dosw.parcial.persistence.repositories;

import edu.dosw.parcial.persistence.entities.BarberDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BarberDocumentRepository extends MongoRepository<BarberDocument, String> {
    Optional<BarberDocument> findByName(String name);
}
