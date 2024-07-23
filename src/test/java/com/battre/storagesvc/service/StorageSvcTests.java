package com.battre.storagesvc.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

import com.battre.storagesvc.model.StorageRecordType;
import com.battre.storagesvc.repository.StorageFacilitiesRepository;
import com.battre.storagesvc.repository.StorageRecordsRepository;
import com.battre.stubs.services.BatteryStorageInfo;
import com.battre.stubs.services.StoreBatteryRequest;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = "grpc.server.port=9032")
public class StorageSvcTests {
  @Mock private StorageFacilitiesRepository storageFacRepo;
  @Mock private StorageRecordsRepository storageRecRepo;
  private StorageSvc storageSvc;

  private AutoCloseable closeable;

  @BeforeEach
  public void openMocks() {
    closeable = MockitoAnnotations.openMocks(this);
    storageSvc = new StorageSvc(storageFacRepo, storageRecRepo);
  }

  @AfterEach
  public void releaseMocks() throws Exception {
    closeable.close();
  }

  @Test
  public void testCheckStorageAndAttemptStore_Success() {
    int orderId = 5;
    List<BatteryStorageInfo> incomingBatteries =
            List.of(
                    BatteryStorageInfo.newBuilder().setBatteryId(1).setBatteryTier(1).build(),
                    BatteryStorageInfo.newBuilder().setBatteryId(2).setBatteryTier(2).build(),
                    BatteryStorageInfo.newBuilder().setBatteryId(3).setBatteryTier(2).build(),
                    BatteryStorageInfo.newBuilder().setBatteryId(4).setBatteryTier(3).build());

    StoreBatteryRequest incomingRequest =
            StoreBatteryRequest.newBuilder()
                    .setOrderId(orderId)
                    .addAllBatteries(incomingBatteries)
                    .build();

    // checkStorage
    List<Object[]> availStorage =
            List.of(new Object[] {1, 2L}, new Object[] {2, 4L}, new Object[] {3, 1L});
    doReturn(availStorage).when(storageFacRepo).getStorageStatsForAllTiers();

    doReturn(3).when(storageFacRepo).getAvailStorageIdForTier(1);
    doReturn(5).when(storageFacRepo).getAvailStorageIdForTier(2);
    doReturn(2).when(storageFacRepo).getAvailStorageIdForTier(3);

    boolean result = storageSvc.checkStorageAndAttemptStore(incomingRequest);
    assertTrue(result);

    ArgumentCaptor<Integer> facilityCaptor = ArgumentCaptor.forClass(Integer.class);
    verify(storageFacRepo, times(4)).incrementStorageFacilityUsage(facilityCaptor.capture());
    List<Integer> capturedFacilityVals = facilityCaptor.getAllValues();
    assertEquals(4, capturedFacilityVals.size());
    assertEquals(3, capturedFacilityVals.get(0));
    assertEquals(5, capturedFacilityVals.get(1));
    assertEquals(5, capturedFacilityVals.get(2));
    assertEquals(2, capturedFacilityVals.get(3));

    ArgumentCaptor<StorageRecordType> batteryCaptor =
            ArgumentCaptor.forClass(StorageRecordType.class);
    verify(storageRecRepo, times(4)).save(batteryCaptor.capture());
    List<StorageRecordType> capturedBatteryVals = batteryCaptor.getAllValues();
    assertEquals(4, capturedBatteryVals.size());
    assertEquals(1, capturedBatteryVals.get(0).getBatteryId());
    assertEquals(orderId, capturedBatteryVals.get(0).getIntakeOrderId());
    assertEquals(3, capturedBatteryVals.get(0).getStorageFacilityId());
  }

  @Test
  public void testCheckStorageAndAttemptStore_Fail_NotEnoughStorage() {
    int orderId = 6;
    List<BatteryStorageInfo> incomingBatteries =
            List.of(
                    BatteryStorageInfo.newBuilder().setBatteryId(5).setBatteryTier(3).build(),
                    BatteryStorageInfo.newBuilder().setBatteryId(6).setBatteryTier(4).build(),
                    BatteryStorageInfo.newBuilder().setBatteryId(7).setBatteryTier(4).build());

    StoreBatteryRequest incomingRequest =
            StoreBatteryRequest.newBuilder()
                    .setOrderId(orderId)
                    .addAllBatteries(incomingBatteries)
                    .build();

    // checkStorage
    List<Object[]> availStorage = List.of(new Object[] {3, 2L}, new Object[] {4, 1L});
    doReturn(availStorage).when(storageFacRepo).getStorageStatsForAllTiers();

    boolean result = storageSvc.checkStorageAndAttemptStore(incomingRequest);
    assertFalse(result);
  }

  @Test
  public void testCheckStorageAndAttemptStore_Fail_NonexistentStorage() {
    int orderId = 7;
    List<BatteryStorageInfo> incomingBatteries =
            List.of(
                    BatteryStorageInfo.newBuilder().setBatteryId(8).setBatteryTier(4).build(),
                    BatteryStorageInfo.newBuilder().setBatteryId(9).setBatteryTier(5).build());

    StoreBatteryRequest incomingRequest =
            StoreBatteryRequest.newBuilder()
                    .setOrderId(orderId)
                    .addAllBatteries(incomingBatteries)
                    .build();

    // checkStorage
    List<Object[]> availStorage = List.of(new Object[] {3, 2L}, new Object[] {4, 1L});
    doReturn(availStorage).when(storageFacRepo).getStorageStatsForAllTiers();

    boolean result = storageSvc.checkStorageAndAttemptStore(incomingRequest);
    assertFalse(result);
  }

  @Test
  public void testGetStorageStats() {
    List<Object[]> storageStats = Arrays.asList(
            new Object[]{1, 50, 100},
            new Object[]{2, 30, 60}
    );
    doReturn(storageStats).when(storageFacRepo).getStorageUsageStatsForAllTiers();

    List<List<Integer>> expectedStats = Arrays.asList(
            Arrays.asList(1, 50, 100),
            Arrays.asList(2, 30, 60)
    );

    List<List<Integer>> result = storageSvc.getStorageStats();

    assertEquals(expectedStats, result);
    verify(storageFacRepo).getStorageUsageStatsForAllTiers();
  }

  @Test
  public void testRemoveBattery() {
    int batteryId = 10;

    boolean result = storageSvc.removeBattery(batteryId);

    assertTrue(result);
    verify(storageFacRepo).decrementStorageFacilityUsageForBatteryId(batteryId);
    verify(storageRecRepo).endStorageForBatteryId(eq(batteryId), any(Timestamp.class));
  }
}

