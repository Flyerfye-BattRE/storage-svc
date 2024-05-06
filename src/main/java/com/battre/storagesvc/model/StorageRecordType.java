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
@Table(name = "StorageRecords", schema = "StorageSvcSchema")
public class StorageRecordType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "storage_id")
    private Long storageId;

    @Column(name = "storage_facility_id")
    private Integer storageFacilityId;

    @Column(name = "battery_id")
    private Integer batteryId;

    @Column(name = "intake_order_id")
    private Integer intakeOrderId;

    @Column(name = "storage_start_date")
    private Timestamp storageStartDate;

    @Column(name = "storage_end_date")
    private Timestamp storageEndDate;

    public StorageRecordType(Integer storageFacilityId, Integer batteryId, Integer intakeOrderId) {
        this.storageFacilityId = storageFacilityId;
        this.batteryId = batteryId;
        this.intakeOrderId = intakeOrderId;
        this.storageStartDate = Timestamp.from(Instant.now());
    }

    public Integer getStorageFacilityId() {
        return storageFacilityId;
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

    public Timestamp getStorageStartDate() {
        return storageStartDate;
    }

    public void setStorageStartDate(Timestamp StorageStartDate) {
        this.storageStartDate = StorageStartDate;
    }

    public Timestamp getStorageEndDate() {
        return storageEndDate;
    }

    public void setStorageEndDate(Timestamp StorageEndDate) {
        this.storageEndDate = StorageEndDate;
    }
}