package com.battre.storagesvc.repository;

import com.battre.storagesvc.model.StorageFacilityType;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface StorageFacilitiesRepository extends CrudRepository<StorageFacilityType, Integer> {
    @Query("SELECT batteryTierId, SUM(capacity) - SUM(usage) as unused_storage " +
            "FROM StorageFacilityType " +
            "GROUP BY batteryTierId " +
            "ORDER BY batteryTierId")
    List<Object[]> getUnusedStoragePerTier();


    @Query("SELECT id " +
            "FROM StorageFacilityType " +
            "WHERE batteryTierId = :batteryTier " +
            "   AND usage < capacity " +
            "ORDER BY id ASC")
    int getAvailStorageForTier(@Param("batteryTier") int batteryTier);

    @Transactional
    @Modifying
    @Query("UPDATE StorageFacilityType SET usage = usage + 1 WHERE id = :storageFacilityId")
    void incrementStorageFacilityUsage(@Param("storageFacilityId") int storageFacilityId);
}
