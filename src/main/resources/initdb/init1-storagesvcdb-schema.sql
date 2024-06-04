-- -----------------------------------------------------
-- Schema StorageSvcSchema
-- -----------------------------------------------------
CREATE SCHEMA IF NOT EXISTS StorageSvcSchema;

CREATE TABLE IF NOT EXISTS StorageSvcSchema.StorageFacilities (
  storage_facility_id SERIAL PRIMARY KEY,
  location VARCHAR(45) NOT NULL,
  battery_tier_id INT NOT NULL,
  capacity INT NOT NULL,
  usage INT NOT NULL,
  notes VARCHAR(45)
);

CREATE TABLE IF NOT EXISTS StorageSvcSchema.StorageRecords (
  storage_id SERIAL PRIMARY KEY,
  storage_facility_id INT NOT NULL,
  battery_id INT,
  intake_order_id INT NOT NULL,
  storage_start_date TIMESTAMP NOT NULL,
  storage_end_date TIMESTAMP,
  CONSTRAINT storage_facility_id_fk FOREIGN KEY (storage_facility_id) REFERENCES StorageFacilities(storage_facility_id) ON DELETE NO ACTION ON UPDATE NO ACTION
);