package com.battre.storagesvc.repository;

import com.battre.storagesvc.model.StorageRecordType;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StorageRecordsRepository extends CrudRepository<StorageRecordType, Integer> {
}
