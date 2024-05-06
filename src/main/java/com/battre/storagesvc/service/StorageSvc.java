package com.battre.storagesvc.service;

import com.battre.storagesvc.model.StorageRecordType;
import com.battre.storagesvc.repository.StorageFacilitiesRepository;
import com.battre.storagesvc.repository.StorageRecordsRepository;
import com.battre.stubs.services.BatteryStorageInfo;
import com.battre.stubs.services.StoreBatteryRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Service
public class StorageSvc {
    private static final Logger logger = Logger.getLogger(StorageSvc.class.getName());

    private final StorageFacilitiesRepository storageFacRepo;
    private final StorageRecordsRepository storageRecRepo;

    @Autowired
    public StorageSvc(StorageFacilitiesRepository storageFacRepo,
                      StorageRecordsRepository storageRecRepo) {
        this.storageFacRepo = storageFacRepo;
        this.storageRecRepo = storageRecRepo;
    }

    @Transactional
    public boolean checkStorageAndAttemptStore(StoreBatteryRequest incomingRequest) {
        Map<Integer, Integer> reqStorageForAllTiersMap = calculateReqStorageForAllTiers(incomingRequest.getBatteriesList());
        if (checkStorage(reqStorageForAllTiersMap)) {
            // update all tiers storage space
            for (BatteryStorageInfo battery : incomingRequest.getBatteriesList()) {
                // find storage facility with avail capacity and lowest facility id
                int storageFacilityId = storageFacRepo.getAvailStorageIdForTier(battery.getBatteryTier());
                logger.info("For battery [" + battery.getBatteryId()
                        + "] tier [" + battery.getBatteryTier() + "] => Storing in [" + storageFacilityId + "]");

                // increase usage for that facility
                storageFacRepo.incrementStorageFacilityUsage(storageFacilityId);

                // add batteries to storage records
                StorageRecordType storedBattery = new StorageRecordType(
                        storageFacilityId,
                        battery.getBatteryId(),
                        incomingRequest.getOrderId()
                );

                storageRecRepo.save(storedBattery);
            }
        } else {
            return false;
        }

        return true;
    }

    private boolean checkStorage(Map<Integer, Integer> reqStorageForAllTiersMap) {
        List<Object[]> availStorageForAllTiersList = storageFacRepo.getAvailStorageForAllTiers();
        Map<Integer, Integer> availStorageForAllTiersMap = convertToStorageForAllTiersMap(availStorageForAllTiersList);

        // check the avail storage space for each battery tier
        for (int tier : reqStorageForAllTiersMap.keySet()) {
            // if the storage for an incoming battery tier doesn't exist or is insufficient => exception
            if (!availStorageForAllTiersMap.containsKey(tier)
                    || availStorageForAllTiersMap.get(tier) < reqStorageForAllTiersMap.get(tier)) {
                logger.info("Storage facilities do not contain enough space in specified battery tier [" + tier + "]");
                return false;
            }
        }

        return true;
    }

    private Map<Integer, Integer> calculateReqStorageForAllTiers(List<BatteryStorageInfo> batteryInfoList) {
        Map<Integer, Integer> reqStorageForAllTiers = new HashMap<>();

        for (BatteryStorageInfo battery : batteryInfoList) {
            int tier = battery.getBatteryTier();
            if (!reqStorageForAllTiers.containsKey(tier)) {
                reqStorageForAllTiers.put(tier, 0);
            }
            reqStorageForAllTiers.put(tier, reqStorageForAllTiers.get(tier) + 1);
            logger.info("Updated " + tier + " with " + reqStorageForAllTiers.get(tier));
        }

        return reqStorageForAllTiers;
    }

    @Transactional
    public boolean removeBattery(int batteryId) {
        storageFacRepo.decrementStorageFacilityUsageForBatteryId(batteryId);
        storageRecRepo.endStorageForBatteryId(batteryId, Timestamp.from(Instant.now()));

        return true;
    }

    public static Map<Integer, Integer> convertToStorageForAllTiersMap(List<Object[]> list) {
        return list.stream()
                .collect(Collectors.toMap(
                        arr -> (Integer) arr[0],   // Extract the battery tier id
                        arr -> ((Long) arr[1]).intValue()    // Extract the avail storage value
                ));
    }
}
