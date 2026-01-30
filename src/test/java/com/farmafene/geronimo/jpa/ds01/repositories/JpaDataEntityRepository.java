package com.farmafene.geronimo.jpa.ds01.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.farmafene.geronimo.jpa.ds01.entities.DataEntity;

@Repository
public interface JpaDataEntityRepository extends JpaRepository<DataEntity, String> {
}
