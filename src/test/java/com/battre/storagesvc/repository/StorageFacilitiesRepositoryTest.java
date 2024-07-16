package com.battre.storagesvc.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.stream.Collectors;

@ExtendWith(SpringExtension.class)
@DataJpaTest
public class StorageFacilitiesRepositoryTest {
  private static final Logger logger =
          Logger.getLogger(StorageFacilitiesRepositoryTest.class.getName());

  @Autowired private StorageFacilitiesRepository storageFacRepo;

  @Test
  public void testGetStorageStatsForAllTiers() {
    List<Object[]> availStorageForAllTiersList = storageFacRepo.getStorageStatsForAllTiers();
    Map<Integer, Integer> availStorageForAllTiersMap =
            convertToAvailStorageForAllTiersMap(availStorageForAllTiersList);

    // Verify the result
    assertEquals(3, availStorageForAllTiersMap.entrySet().size());

    assertTrue(availStorageForAllTiersMap.containsKey(3));
    assertEquals(3, availStorageForAllTiersMap.get(3));
    assertTrue(availStorageForAllTiersMap.containsKey(6));
    assertEquals(4, availStorageForAllTiersMap.get(6));
    assertTrue(availStorageForAllTiersMap.containsKey(7));
    assertEquals(2, availStorageForAllTiersMap.get(7));
  }

  @Test
  public void testGetAvailStorageForTier() {
    int availStorageForTier = storageFacRepo.getAvailStorageForTier(3);

    // Verify the result
    assertEquals(3, availStorageForTier);
  }

  @Test
  public void testGetAvailStorageIdForTier() {
    int availStorageId = storageFacRepo.getAvailStorageIdForTier(6);

    // Verify the result
    assertEquals(3, availStorageId);
  }

  @Test
  public void testIncrementStorageFacilityUsage() {
    storageFacRepo.incrementStorageFacilityUsage(2);

    // Verify the result
    int availStorage = storageFacRepo.getAvailStorageForTier(7);
    assertEquals(1, availStorage);
  }

  @Test
  public void testDecrementStorageFacilityUsage() {
    storageFacRepo.decrementStorageFacilityUsage(3);

    // Verify the result
    int availStorage = storageFacRepo.getAvailStorageForTier(6);
    assertEquals(5, availStorage);
  }

  @Test
  public void testDecrementStorageFacilityUsageForBatteryId() {
    int batteryId = 5;

    storageFacRepo.decrementStorageFacilityUsageForBatteryId(batteryId);

    int availStorage = storageFacRepo.getAvailStorageForTier(6);
    assertEquals(5, availStorage);  // This value depends on the initial state of the database
  }

  private Map<Integer, Integer> convertToAvailStorageForAllTiersMap(List<Object[]> list) {
    return list.stream()
            .collect(Collectors.toMap(
                    arr -> (Integer) arr[0], // Extract the battery tier id
                    arr -> ((Long) arr[1]).intValue() // Extract the avail storage value
            ));
  }
}
