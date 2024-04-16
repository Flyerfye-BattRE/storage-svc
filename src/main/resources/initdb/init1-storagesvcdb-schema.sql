-- -----------------------------------------------------
-- Schema StorageSvcDb
-- -----------------------------------------------------
CREATE SCHEMA IF NOT EXISTS StorageSvcDb;

CREATE TABLE IF NOT EXISTS StorageSvcDb.StorageFacilities (
  storage_facility_id SERIAL PRIMARY KEY,
  location VARCHAR(45) NOT NULL,
  battery_tier_id INT NOT NULL,
  capacity INT NOT NULL,
  usage INT NOT NULL,
  notes VARCHAR(45)
);

CREATE TABLE IF NOT EXISTS StorageSvcDb.StorageRecords (
  storage_id SERIAL PRIMARY KEY,
  storage_facility_id INT NOT NULL,
  battery_id INT,
  intake_order_id INT NOT NULL,
  storage_start TIMESTAMP NOT NULL,
  storage_end TIMESTAMP,
  CONSTRAINT storage_facility_id FOREIGN KEY (storage_facility_id) REFERENCES StorageSvcDb.StorageFacilities(storage_facility_id) ON DELETE NO ACTION ON UPDATE NO ACTION
);