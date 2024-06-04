package com.battre.storagesvc.repository;

import com.battre.storagesvc.model.StorageFacilityType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface StorageFacilitiesRepository extends JpaRepository<StorageFacilityType, Integer> {
    @Query("SELECT batteryTierId, SUM(capacity) - SUM(usage) as availStorage, SUM(capacity) as totalCapacity " +
            "FROM StorageFacilityType " +
            "GROUP BY batteryTierId " +
            "ORDER BY batteryTierId")
    List<Object[]> getStorageStatsForAllTiers();

    @Query("SELECT SUM(capacity) - SUM(usage) as availStorage " +
            "FROM StorageFacilityType " +
            "WHERE batteryTierId = :batteryTier " +
            "GROUP BY batteryTierId " +
            "ORDER BY batteryTierId")
    int getAvailStorageForTier(@Param("batteryTier") int batteryTier);

    @Query("SELECT storageFacilityId " +
            "FROM StorageFacilityType " +
            "WHERE batteryTierId = :batteryTier " +
            "   AND usage < capacity " +
            "ORDER BY id ASC " +
            "LIMIT 1")
    int getAvailStorageIdForTier(@Param("batteryTier") int batteryTier);

    @Transactional
    @Modifying
    @Query("UPDATE StorageFacilityType SET usage = usage + 1 WHERE storageFacilityId = :storageFacilityId")
    void incrementStorageFacilityUsage(@Param("storageFacilityId") int storageFacilityId);

    @Transactional
    @Modifying
    @Query("UPDATE StorageFacilityType SET usage = usage - 1 WHERE storageFacilityId = :storageFacilityId")
    void decrementStorageFacilityUsage(@Param("storageFacilityId") int storageFacilityId);

    @Transactional
    @Modifying
    @Query("UPDATE StorageFacilityType " +
            "SET usage = usage - 1 " +
            "WHERE storageFacilityId = (" +
                "SELECT storageFacilityId " +
                "FROM StorageRecordType " +
                "WHERE batteryId = :batteryId" +
            ") ")
    void decrementStorageFacilityUsageForBatteryId(@Param("batteryId") int batteryId);
}
