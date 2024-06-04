package com.battre.storagesvc.controller;

import com.battre.storagesvc.service.StorageSvc;
import com.battre.stubs.services.StoreBatteryRequest;
import com.battre.stubs.services.StoreBatteryResponse;
import io.grpc.stub.StreamObserver;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class StorageSvcControllerTests {
    @Mock
    private StorageSvc storageSvc;

    @Mock
    private StreamObserver<StoreBatteryResponse> responseStoreBatteryResponse;

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
        verify(responseStoreBatteryResponse).onNext(StoreBatteryResponse.newBuilder().setSuccess(true).build());
        verify(responseStoreBatteryResponse).onCompleted();
    }

    @Test
    void testTryStoreBatteriesFail() {
        storageSvcController = new StorageSvcController(storageSvc);

        StoreBatteryRequest request = StoreBatteryRequest.newBuilder().build();
        when(storageSvc.checkStorageAndAttemptStore(request)).thenReturn(false);

        storageSvcController.tryStoreBatteries(request, responseStoreBatteryResponse);

        verify(storageSvc).checkStorageAndAttemptStore(request);
        verify(responseStoreBatteryResponse).onNext(StoreBatteryResponse.newBuilder().setSuccess(false).build());
        verify(responseStoreBatteryResponse).onCompleted();
    }

    @Test
    void testRemoveStorageBattery() {
        // TODO: Implement test
    }

    @Test
    void testGetStorageStats() {
        // TODO: Implement test
    }
}
