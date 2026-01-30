package com.farmafene.geronimo.jpa.ds01.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.farmafene.geronimo.jpa.ds01.entities.SerialGenEntity;
import com.farmafene.geronimo.jpa.ds01.entities.SerialGenEntityPK;

@Repository
public interface JpaSerialGenEntityRepository extends JpaRepository<SerialGenEntity, SerialGenEntityPK> {
}
