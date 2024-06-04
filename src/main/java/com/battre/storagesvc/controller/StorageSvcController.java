package com.battre.storagesvc.controller;

import com.battre.storagesvc.service.StorageSvc;
import com.battre.stubs.services.GetStorageStatsRequest;
import com.battre.stubs.services.GetStorageStatsResponse;
import com.battre.stubs.services.RemoveStorageBatteryRequest;
import com.battre.stubs.services.RemoveStorageBatteryResponse;
import com.battre.stubs.services.StorageSvcGrpc;
import com.battre.stubs.services.StoreBatteryRequest;
import com.battre.stubs.services.StoreBatteryResponse;
import com.battre.stubs.services.TierStats;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
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
    public void removeStorageBattery(RemoveStorageBatteryRequest request, StreamObserver<RemoveStorageBatteryResponse> responseObserver) {
        int batteryId = request.getBatteryId();
        logger.info("removeBattery() invoked for [" + batteryId + "]");

        boolean removeBatterySuccess = false;

        removeBatterySuccess = storageSvc.removeBattery(batteryId);
        RemoveStorageBatteryResponse response = RemoveStorageBatteryResponse.newBuilder()
                .setSuccess(removeBatterySuccess)
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();

        logger.info("removeBattery() finished");
    }

    @Override
    public void getStorageStats(GetStorageStatsRequest request, StreamObserver<GetStorageStatsResponse> responseObserver) {
        logger.info("getStorageStats() invoked");

        List<List<Integer>> storageStats = storageSvc.getStorageStats();

        GetStorageStatsResponse.Builder responseBuilder = GetStorageStatsResponse.newBuilder();
        for (List<Integer> tierStats : storageStats) {
            TierStats.Builder tierStatsBuilder = TierStats.newBuilder()
                    .setBatteryTierId(tierStats.get(0))
                    .setAvailStorage(tierStats.get(1))
                    .setCapacity(tierStats.get(2));

            responseBuilder.addTierStatsList(tierStatsBuilder.build());
        }

        GetStorageStatsResponse response = responseBuilder.build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();

        logger.info("getStorageStats() finished");
    }
}
