package com.battre.storagesvc.repository;

import com.battre.storagesvc.model.StorageRecordType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;

@Repository
public interface StorageRecordsRepository extends JpaRepository<StorageRecordType, Integer> {
    @Transactional
    @Modifying
    @Query("UPDATE StorageRecordType " +
            "SET storageEndDate = :storageEndDate " +
            "WHERE batteryId = :batteryId")
    void endStorageForBatteryId(@Param("batteryId") int batteryId,
                                @Param("storageEndDate") Timestamp storageEndDate);
}
