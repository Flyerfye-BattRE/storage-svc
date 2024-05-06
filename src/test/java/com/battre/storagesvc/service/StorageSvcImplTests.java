package com.battre.storagesvc.service;

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

public class StorageSvcImplTests {
    @Mock
    private StorageSvc storageSvc;

    @Mock
    private StreamObserver<StoreBatteryResponse> responseStoreBatteryResponse;

    private StorageSvcImpl storageSvcImpl;

    private AutoCloseable closeable;

    @BeforeEach
    public void openMocks() {
        closeable = MockitoAnnotations.openMocks(this);
        storageSvcImpl = new StorageSvcImpl(storageSvc);
    }

    @AfterEach
    public void releaseMocks() throws Exception {
        closeable.close();
    }

    @Test
    void testTryStoreBatteriesSuccess() {
        storageSvcImpl = new StorageSvcImpl(storageSvc);

        StoreBatteryRequest request = StoreBatteryRequest.newBuilder().build();
        when(storageSvc.checkStorageAndAttemptStore(request)).thenReturn(true);

        storageSvcImpl.tryStoreBatteries(request, responseStoreBatteryResponse);

        verify(storageSvc).checkStorageAndAttemptStore(request);
        verify(responseStoreBatteryResponse).onNext(StoreBatteryResponse.newBuilder().setSuccess(true).build());
        verify(responseStoreBatteryResponse).onCompleted();
    }

    @Test
    void testTryStoreBatteriesFail() {
        storageSvcImpl = new StorageSvcImpl(storageSvc);

        StoreBatteryRequest request = StoreBatteryRequest.newBuilder().build();
        when(storageSvc.checkStorageAndAttemptStore(request)).thenReturn(false);

        storageSvcImpl.tryStoreBatteries(request, responseStoreBatteryResponse);

        verify(storageSvc).checkStorageAndAttemptStore(request);
        verify(responseStoreBatteryResponse).onNext(StoreBatteryResponse.newBuilder().setSuccess(false).build());
        verify(responseStoreBatteryResponse).onCompleted();
    }
}