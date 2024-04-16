package com.battre.storagesvc.service;

import com.battre.stubs.services.StorageSvcGrpc;
import com.battre.stubs.services.StoreBatteryRequest;
import com.battre.stubs.services.StoreBatteryResponse;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.logging.Logger;

@GrpcService
public class StorageSvcImpl extends StorageSvcGrpc.StorageSvcImplBase {
    private static final Logger logger = Logger.getLogger(StorageSvcImpl.class.getName());

    private final StorageService storageService;

    @Autowired
    public StorageSvcImpl(StorageService storageService) {
        this.storageService = storageService;
    }

    @Override
    public void tryStoreBatteries(StoreBatteryRequest request, StreamObserver<StoreBatteryResponse> responseObserver) {
        logger.info("tryStoreBatteries() invoked");
        logger.info("request: " + request);

        boolean storageSuccess = false;

        try {
            storageSuccess = storageService.checkStorageAndAttemptStore(request);
        } catch (StorageService.InsufficientStorageSpaceException e) {
            storageSuccess = false;
        }

        StoreBatteryResponse response = StoreBatteryResponse.newBuilder()
                .setSuccess(storageSuccess)
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();

        logger.info("tryStoreBatteries() finished");
    }

}
