package edu.dosw.parcial.persistence.repositories;

import edu.dosw.parcial.persistence.entities.UserDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends MongoRepository<UserDocument, String> {

    Optional<UserDocument> findByUsername(String username);

    Optional<UserDocument> findByEmail(String email);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);
}
