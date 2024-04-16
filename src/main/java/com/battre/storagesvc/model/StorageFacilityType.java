package com.battre.storagesvc.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "StorageFacilities", schema = "StorageSvcDb")
public class StorageFacilityType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "storage_facility_id")
    private Long id;

    @Column(name = "location")
    private String location;

    @Column(name = "battery_tier_id")
    private Integer batteryTierId;

    @Column(name = "capacity")
    private Integer capacity;

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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Integer getBatteryTierId() {
        return batteryTierId;
    }

    public void setBatteryTierId(Integer batteryTierId) {
        this.batteryTierId = batteryTierId;
    }

    public Integer getCapacity() {
        return capacity;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
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
