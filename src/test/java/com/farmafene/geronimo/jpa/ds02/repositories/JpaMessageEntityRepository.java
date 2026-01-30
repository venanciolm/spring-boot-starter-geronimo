package com.farmafene.geronimo.jpa.ds02.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.farmafene.geronimo.jpa.ds02.entities.MessageEntity;

@Repository
public interface JpaMessageEntityRepository extends JpaRepository<MessageEntity, String> {
}
