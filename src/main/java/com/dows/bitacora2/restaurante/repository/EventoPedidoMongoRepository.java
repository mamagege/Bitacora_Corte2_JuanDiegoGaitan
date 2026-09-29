package com.dows.bitacora2.restaurante.repository;

import com.dows.bitacora2.restaurante.persistence.document.EventoPedidoDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EventoPedidoMongoRepository extends MongoRepository<EventoPedidoDocument, String> {
    List<EventoPedidoDocument> findByPedidoIdOrderByFechaEventoDesc(Long pedidoId);
}
