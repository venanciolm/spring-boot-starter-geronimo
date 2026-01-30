package com.farmafene.geronimo.jpa.ds01.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.farmafene.geronimo.jpa.ds01.entities.SerialEntity;
import com.farmafene.geronimo.jpa.ds01.entities.SerialEntityPK;

@Repository
public interface JpaSerialEntityRepository extends JpaRepository<SerialEntity, SerialEntityPK> {
}
