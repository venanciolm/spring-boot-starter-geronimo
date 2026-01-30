package com.farmafene.geronimo.jpa.ds02.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.farmafene.geronimo.jpa.ds02.entities.AsyncMessageClobEntity;

@Repository
public interface JpaAsyncMsgClobEntityRepository extends JpaRepository<AsyncMessageClobEntity, String> {
}
