package com.battre.storagesvc.controller;

import com.battre.storagesvc.service.StorageSvc;
import com.battre.stubs.services.RemoveBatteryRequest;
import com.battre.stubs.services.RemoveBatteryResponse;
import com.battre.stubs.services.StorageSvcGrpc;
import com.battre.stubs.services.StoreBatteryRequest;
import com.battre.stubs.services.StoreBatteryResponse;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.logging.Logger;

@GrpcService
public class StorageSvcController extends StorageSvcGrpc.StorageSvcImplBase {
    private static final Logger logger = Logger.getLogger(StorageSvcController.class.getName());

    private final StorageSvc storageSvc;

    @Autowired
    public StorageSvcController(StorageSvc storageSvc) {
        this.storageSvc = storageSvc;
    }

    @Override
    public void tryStoreBatteries(StoreBatteryRequest request, StreamObserver<StoreBatteryResponse> responseObserver) {
        logger.info("tryStoreBatteries() invoked");
        logger.info("request: " + request);

        boolean storageSuccess = false;

        storageSuccess = storageSvc.checkStorageAndAttemptStore(request);
        StoreBatteryResponse response = StoreBatteryResponse.newBuilder()
                .setSuccess(storageSuccess)
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();

        logger.info("tryStoreBatteries() finished");
    }

    @Override
    public void removeBattery(RemoveBatteryRequest request, StreamObserver<RemoveBatteryResponse> responseObserver) {
        int batteryId = request.getBatteryId();
        logger.info("removeBattery() invoked for [" + batteryId + "]");

        boolean removeBatterySuccess = false;

        removeBatterySuccess = storageSvc.removeBattery(batteryId);
        RemoveBatteryResponse response = RemoveBatteryResponse.newBuilder()
                .setSuccess(removeBatterySuccess)
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();

        logger.info("removeBattery() finished");
    }

}
