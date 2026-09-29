package com.dows.bitacora2.restaurante.repository;

import com.dows.bitacora2.restaurante.persistence.entity.MesaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MesaRepository extends JpaRepository<MesaEntity, Long> {
}
