package com.battre.storagesvc.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.sql.Timestamp;
import java.time.Instant;

@Entity
@Table(name = "StorageRecords", schema = "StorageSvcDb")
public class StorageRecordType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "storage_id")
    private Long id;

    @Column(name = "storage_facility_id")
    private Integer storageFacilityId;

    @Column(name = "battery_id")
    private Integer batteryId;

    @Column(name = "intake_order_id")
    private Integer intakeOrderId;

    @Column(name = "storage_start")
    private Timestamp storageStart;

    @Column(name = "storage_end")
    private Timestamp storageEnd;

    public StorageRecordType(Integer storageFacilityId, Integer batteryId, Integer intakeOrderId) {
        this.storageFacilityId = storageFacilityId;
        this.batteryId = batteryId;
        this.intakeOrderId = intakeOrderId;
        this.storageStart = Timestamp.from(Instant.now());
    }

    public Integer getStorageFacilityId() {
        return storageFacilityId;
    }

    public void setStorageFacilityId(Integer storageFacilityId) {
        this.storageFacilityId = storageFacilityId;
    }

    public Integer getBatteryId() {
        return batteryId;
    }

    public void setBatteryId(Integer batteryId) {
        this.batteryId = batteryId;
    }

    public Integer getIntakeOrderId() {
        return intakeOrderId;
    }

    public void setIntakeOrderId(Integer intakeOrderId) {
        this.intakeOrderId = intakeOrderId;
    }

    public Timestamp getStorageStart() {
        return storageStart;
    }

    public void setStorageStart(Timestamp storageStart) {
        this.storageStart = storageStart;
    }

    public Timestamp getStorageEnd() {
        return storageEnd;
    }

    public void setStorageEnd(Timestamp storageEnd) {
        this.storageEnd = storageEnd;
    }
}