package edu.dosw.parcial.persistence.repositories;

import edu.dosw.parcial.persistence.entities.TurnoDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TurnoRepository extends MongoRepository<TurnoDocument, String> {

    Optional<TurnoDocument> findByTurnoId(String turnoId);

    long countByTimeSlotAndStatus(String timeSlot, String status);

    long countByTimeSlot(String timeSlot);

    List<TurnoDocument> findByClientEmail(String clientEmail);

    List<TurnoDocument> findByStatus(String status);
}
