package com.battre.storagesvc.service;

import com.battre.storagesvc.model.StorageRecordType;
import com.battre.storagesvc.repository.StorageFacilitiesRepository;
import com.battre.storagesvc.repository.StorageRecordsRepository;
import com.battre.stubs.services.BatteryStorageInfo;
import com.battre.stubs.services.StorageSvcGrpc;
import com.battre.stubs.services.StoreBatteryRequest;
import com.battre.stubs.services.StoreBatteryResponse;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@GrpcService
public class StorageSvcImpl extends StorageSvcGrpc.StorageSvcImplBase {
    private static final Logger logger = Logger.getLogger(StorageSvcImpl.class.getName());

    private final StorageFacilitiesRepository storageFacilitiesRepository;
    private final StorageRecordsRepository storageRecordsRepository;

    @Autowired
    public StorageSvcImpl(StorageFacilitiesRepository storageFacilitiesRepository,
                          StorageRecordsRepository storageRecordsRepository) {
        this.storageFacilitiesRepository = storageFacilitiesRepository;
        this.storageRecordsRepository = storageRecordsRepository;
    }

    @Override
    public void tryStoreBatteries(StoreBatteryRequest incomingRequest, StreamObserver<StoreBatteryResponse> outgoingResponse) {
        logger.info("tryStoreBatteries invoked");
        logger.info("request: " + incomingRequest);

        boolean storageSuccess = false;

        try {
            storageSuccess = checkStorageAndAttemptStore(incomingRequest);
        } catch (InsufficientStorageSpaceException e) {
            storageSuccess = false;
        }

        StoreBatteryResponse myResponse = StoreBatteryResponse.newBuilder()
                .setSuccess(storageSuccess)
                .build();

        outgoingResponse.onNext(myResponse);
        outgoingResponse.onCompleted();

        logger.info("tryStoreBatteries finished");
    }

    @Transactional
    private boolean checkStorageAndAttemptStore(StoreBatteryRequest incomingRequest) throws InsufficientStorageSpaceException {
        Map<Integer, Integer> reqStoragePerTierMap = calculateReqStoragePerTier(incomingRequest.getBatteriesList());
        if(checkStorage(reqStoragePerTierMap)) {
            // update all tiers storage space
            for(BatteryStorageInfo battery:incomingRequest.getBatteriesList()) {
                // find storage facility with unused capacity and lowest facility id
                int storageFacilityId = storageFacilitiesRepository.getAvailStorageForTier(battery.getBatteryTier());
                logger.info("For battery ["+battery.getBatteryId()
                        +"] tier ["+battery.getBatteryTier()+"] => Storing in ["+storageFacilityId+"]");

                // increase usage for that facility
                storageFacilitiesRepository.incrementStorageFacilityUsage(storageFacilityId);

                // add batteries to storage records
                StorageRecordType storedBattery = new StorageRecordType(
                        storageFacilityId,
                        battery.getBatteryId(),
                        incomingRequest.getOrderId()
                );

                storageRecordsRepository.save(storedBattery);
            }
        } else {
            return false;
        }

        return true;
    }

    private boolean checkStorage(Map<Integer, Integer> reqStoragePerTierMap) throws InsufficientStorageSpaceException {
        List<Object[]> unusedStoragePerTierList = storageFacilitiesRepository.getUnusedStoragePerTier();
        Map<Integer, Integer> unusedStoragePerTierMap = convertObjectArrayToMap(unusedStoragePerTierList);

        // check the unused storage space for each battery tier
        for(int tier:reqStoragePerTierMap.keySet()) {
            // if the storage for an incoming battery tier doesn't exist or is insufficient => exception
            if(!unusedStoragePerTierMap.containsKey(tier)
                    || unusedStoragePerTierMap.get(tier) < reqStoragePerTierMap.get(tier)) {
                logger.info("Storage facilities do not contain enough space in specified battery tier [" + tier + "]");
                throw new InsufficientStorageSpaceException(tier);
            }
        }

        return true;
    }

    private Map<Integer, Integer> calculateReqStoragePerTier(List<BatteryStorageInfo> batteryInfoList) {
        Map<Integer, Integer> reqStoragePerTier = new HashMap<>();

        for(BatteryStorageInfo battery:batteryInfoList) {
            int tier = battery.getBatteryTier();
            if(!reqStoragePerTier.containsKey(tier)) {
                reqStoragePerTier.put(tier, 0);
            }
            reqStoragePerTier.put(tier, reqStoragePerTier.get(tier) + 1);
            logger.info("Updated " + tier + " with " + reqStoragePerTier.get(tier));
        }

        return reqStoragePerTier;
    }

    private Map<Integer, Integer> convertObjectArrayToMap(List<Object[]> list) {
        return list.stream()
                .collect(Collectors.toMap(
                        arr -> (Integer) arr[0],   // Extract the battery tier id
                        arr -> ((Long) arr[1]).intValue()    // Extract the unused storage value
                ));
    }

    public class InsufficientStorageSpaceException extends Exception {

        public InsufficientStorageSpaceException() {
            super("Storage facilities do not contain enough space in specified battery tier");
        }

        public InsufficientStorageSpaceException(int tier) {
            super("Storage facilities do not contain enough space in specified battery tier [" + tier + "]");
        }
    }
}
