package com.battre.storagesvc.controller;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.battre.storagesvc.service.StorageSvc;
import com.battre.stubs.services.GetStorageStatsRequest;
import com.battre.stubs.services.GetStorageStatsResponse;
import com.battre.stubs.services.RemoveStorageBatteryRequest;
import com.battre.stubs.services.RemoveStorageBatteryResponse;
import com.battre.stubs.services.StoreBatteryRequest;
import com.battre.stubs.services.StoreBatteryResponse;
import com.battre.stubs.services.TierStats;
import com.google.protobuf.Int32Value;
import io.grpc.stub.StreamObserver;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;

public class StorageSvcControllerTests {
  @Mock private StorageSvc storageSvc;

  @Mock private StreamObserver<StoreBatteryResponse> responseStoreBatteryResponse;
  @Mock private StreamObserver<RemoveStorageBatteryResponse> responseRemoveStorageBatteryResponse;
  @Mock private StreamObserver<GetStorageStatsResponse> responseGetStorageStatsResponse;

  private StorageSvcController storageSvcController;

  private AutoCloseable closeable;

  @BeforeEach
  public void openMocks() {
    closeable = MockitoAnnotations.openMocks(this);
    storageSvcController = new StorageSvcController(storageSvc);
  }

  @AfterEach
  public void releaseMocks() throws Exception {
    closeable.close();
  }

  @Test
  void testTryStoreBatteriesSuccess() {
    storageSvcController = new StorageSvcController(storageSvc);

    StoreBatteryRequest request = StoreBatteryRequest.newBuilder().build();
    when(storageSvc.checkStorageAndAttemptStore(request)).thenReturn(true);

    storageSvcController.tryStoreBatteries(request, responseStoreBatteryResponse);

    verify(storageSvc).checkStorageAndAttemptStore(request);
    verify(responseStoreBatteryResponse)
            .onNext(StoreBatteryResponse.newBuilder().setSuccess(true).build());
    verify(responseStoreBatteryResponse).onCompleted();
  }

  @Test
  void testTryStoreBatteriesFail() {
    storageSvcController = new StorageSvcController(storageSvc);

    StoreBatteryRequest request = StoreBatteryRequest.newBuilder().build();
    when(storageSvc.checkStorageAndAttemptStore(request)).thenReturn(false);

    storageSvcController.tryStoreBatteries(request, responseStoreBatteryResponse);

    verify(storageSvc).checkStorageAndAttemptStore(request);
    verify(responseStoreBatteryResponse)
            .onNext(StoreBatteryResponse.newBuilder().setSuccess(false).build());
    verify(responseStoreBatteryResponse).onCompleted();
  }

  @Test
  void testRemoveStorageBattery() {
    storageSvcController = new StorageSvcController(storageSvc);

    int batteryId = 1;
    RemoveStorageBatteryRequest request = RemoveStorageBatteryRequest.newBuilder().setBatteryId(batteryId).build();
    when(storageSvc.removeBattery(batteryId)).thenReturn(true);

    storageSvcController.removeStorageBattery(request, responseRemoveStorageBatteryResponse);

    verify(storageSvc).removeBattery(batteryId);
    verify(responseRemoveStorageBatteryResponse).onNext(RemoveStorageBatteryResponse.newBuilder().setSuccess(true).build());
    verify(responseRemoveStorageBatteryResponse).onCompleted();
  }

  @Test
  void testGetStorageStats() {
    storageSvcController = new StorageSvcController(storageSvc);

    GetStorageStatsRequest request = GetStorageStatsRequest.newBuilder().build();
    List<List<Integer>> storageStats = Arrays.asList(
            Arrays.asList(1, 50, 100),
            Arrays.asList(2, 30, 60)
    );
    when(storageSvc.getStorageStats()).thenReturn(storageStats);

    storageSvcController.getStorageStats(request, responseGetStorageStatsResponse);

    GetStorageStatsResponse.Builder responseBuilder = GetStorageStatsResponse.newBuilder();
    for (List<Integer> tierStats : storageStats) {
      TierStats.Builder tierStatsBuilder = TierStats.newBuilder()
              .setBatteryTierId(tierStats.get(0))
              .setUsedStorage(Int32Value.of(tierStats.get(1)))
              .setCapacity(tierStats.get(2));
      responseBuilder.addTierStatsList(tierStatsBuilder.build());
    }
    GetStorageStatsResponse response = responseBuilder.build();

    verify(storageSvc).getStorageStats();
    verify(responseGetStorageStatsResponse).onNext(response);
    verify(responseGetStorageStatsResponse).onCompleted();
  }
}
