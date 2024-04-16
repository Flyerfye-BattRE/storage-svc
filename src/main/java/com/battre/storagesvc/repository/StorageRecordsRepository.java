package com.battre.storagesvc.repository;

import com.battre.storagesvc.model.StorageRecordType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StorageRecordsRepository extends JpaRepository<StorageRecordType, Integer> {
}
