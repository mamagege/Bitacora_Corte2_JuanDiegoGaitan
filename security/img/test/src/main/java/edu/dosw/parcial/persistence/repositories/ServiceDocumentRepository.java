package edu.dosw.parcial.persistence.repositories;

import edu.dosw.parcial.persistence.entities.ServiceDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ServiceDocumentRepository extends MongoRepository<ServiceDocument, String> {
    Optional<ServiceDocument> findByCode(String code);
}
