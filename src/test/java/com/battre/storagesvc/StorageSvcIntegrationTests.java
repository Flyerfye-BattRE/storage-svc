package com.battre.storagesvc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.battre.grpcifc.GrpcTestMethodInvoker;
import com.battre.storagesvc.controller.StorageSvcController;
import com.battre.storagesvc.model.StorageRecordType;
import com.battre.storagesvc.repository.StorageFacilitiesRepository;
import com.battre.storagesvc.repository.StorageRecordsRepository;
import com.battre.stubs.services.BatteryStorageInfo;
import com.battre.stubs.services.GetStorageStatsRequest;
import com.battre.stubs.services.GetStorageStatsResponse;
import com.battre.stubs.services.RemoveStorageBatteryRequest;
import com.battre.stubs.services.RemoveStorageBatteryResponse;
import com.battre.stubs.services.StoreBatteryRequest;
import com.battre.stubs.services.StoreBatteryResponse;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@SpringBootTest(properties = "grpc.server.port=9033")
public class StorageSvcIntegrationTests {
  private static final Logger logger = Logger.getLogger(StorageSvcIntegrationTests.class.getName());

  @MockBean private StorageFacilitiesRepository storageFacilitiesRepository;
  @MockBean private StorageRecordsRepository storageRecordsRepository;
  @Autowired private StorageSvcController storageSvcController;
  private final GrpcTestMethodInvoker grpcTestMethodInvoker = new GrpcTestMethodInvoker();

  @Test
  public void testTryStoreBatteries_Success() throws NoSuchMethodException {
    // Test
    // Mock the repository responses
    when(storageFacilitiesRepository.getAvailStorageIdForTier(anyInt())).thenReturn(1);
    List<Object[]> storageStatsForAllTiersList =
        Arrays.asList(new Object[] {1, 1L, 2L}, new Object[] {2, 1L, 2L});
    when(storageFacilitiesRepository.getStorageStatsForAllTiers())
        .thenReturn(storageStatsForAllTiersList);
    doNothing().when(storageFacilitiesRepository).incrementStorageFacilityUsage(anyInt());
    when(storageRecordsRepository.save(any(StorageRecordType.class)))
        .thenReturn(new StorageRecordType());

    // Request
    StoreBatteryRequest request =
        StoreBatteryRequest.newBuilder()
            .addBatteries(BatteryStorageInfo.newBuilder().setBatteryId(1).setBatteryTier(1).build())
            .setOrderId(1001)
            .build();
    StoreBatteryResponse response =
        grpcTestMethodInvoker.invokeNonblock(storageSvcController, "tryStoreBatteries", request);
    assertTrue(response.getSuccess());

    // Verify that repository methods were called
    verify(storageFacilitiesRepository).getAvailStorageIdForTier(anyInt());
    verify(storageFacilitiesRepository).getStorageStatsForAllTiers();
    verify(storageFacilitiesRepository).incrementStorageFacilityUsage(anyInt());
    verify(storageRecordsRepository).save(any(StorageRecordType.class));
  }

  @Test
  public void testRemoveStorageBattery_Success() throws NoSuchMethodException {
    // Test
    doNothing()
        .when(storageFacilitiesRepository)
        .decrementStorageFacilityUsageForBatteryId(anyInt());
    doNothing()
        .when(storageRecordsRepository)
        .endStorageForBatteryId(anyInt(), any(Timestamp.class));

    // Request
    RemoveStorageBatteryRequest request =
        RemoveStorageBatteryRequest.newBuilder().setBatteryId(1).build();
    RemoveStorageBatteryResponse response =
        grpcTestMethodInvoker.invokeNonblock(storageSvcController, "removeStorageBattery", request);
    assertTrue(response.getSuccess());

    // Verify that repository methods were called
    verify(storageFacilitiesRepository).decrementStorageFacilityUsageForBatteryId(anyInt());
    verify(storageRecordsRepository).endStorageForBatteryId(anyInt(), any(Timestamp.class));
  }

  @Test
  public void testGetStorageStats_Success() throws NoSuchMethodException {
    // Test
    when(storageFacilitiesRepository.getStorageStatsForAllTiers())
        .thenReturn(List.of(new Object[] {1, 10L}, new Object[] {2, 20L}));
    when(storageFacilitiesRepository.getStorageUsageStatsForAllTiers())
        .thenReturn(List.of(new Object[] {1, 5, 10}, new Object[] {2, 15, 20}));

    // Request
    GetStorageStatsRequest request = GetStorageStatsRequest.newBuilder().build();
    GetStorageStatsResponse response =
        grpcTestMethodInvoker.invokeNonblock(storageSvcController, "getStorageStats", request);
    assertEquals(response.getTierStatsListCount(), 2);
    assertEquals(1, response.getTierStatsList(0).getBatteryTierId());
    assertEquals(5, response.getTierStatsList(0).getUsedStorage().getValue());
    assertEquals(10, response.getTierStatsList(0).getCapacity());

    // Verify that repository methods were called
    verify(storageFacilitiesRepository).getStorageUsageStatsForAllTiers();
  }
}
