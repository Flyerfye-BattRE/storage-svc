-- Insert statements for the "StorageFacilities" table
INSERT INTO StorageSvcSchema.StorageFacilities (storage_facility_id, location, battery_tier_id, capacity, usage, notes)
SELECT * FROM (VALUES
    (1, 'Test Storage 3a', 3, 1, 0, 'TestNote'),
    (2, 'Test Storage 7', 7, 2, 0, 'TestNote'),
    (3, 'Test Storage 6', 6, 5, 1, 'TestNote'),
    (4, 'Test Storage 3b', 3, 2, 0, 'TestNote')
) AS v (storage_facility_id, location, battery_tier_id, capacity, usage, notes)
WHERE NOT EXISTS (
    SELECT 1 FROM StorageSvcSchema.StorageFacilities
);


