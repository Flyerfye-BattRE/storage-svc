package com.battre.storagesvc.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "StorageFacilities", schema = "StorageSvcSchema")
public class StorageFacilityType {
    @Column(name = "location")
    private final String location;
    @Column(name = "battery_tier_id")
    private final Integer batteryTierId;
    @Column(name = "capacity")
    private final Integer capacity;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "storage_facility_id")
    private Long storageFacilityId;
    @Column(name = "usage")
    private Integer usage;

    @Column(name = "notes")
    private String notes;

    public StorageFacilityType(String location, Integer batteryTierId, Integer capacity, Integer usage, String notes) {
        this.location = location;
        this.batteryTierId = batteryTierId;
        this.capacity = capacity;
        this.usage = usage;
        this.notes = notes;
    }

    public Long getStorageFacilityId() {
        return storageFacilityId;
    }

    public String getLocation() {
        return location;
    }

    public Integer getBatteryTierId() {
        return batteryTierId;
    }

    public Integer getCapacity() {
        return capacity;
    }

    public Integer getUsage() {
        return usage;
    }

    public void setUsage(Integer usage) {
        this.usage = usage;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
