-- Insert statements for the "StorageFacilities" table
INSERT INTO StorageSvcSchema.StorageFacilities (storage_facility_id, location, battery_tier_id, capacity, usage, notes)
SELECT * FROM (VALUES
    (1, 'Test Storage 1', 1, 1, 0, 'TestNote'),
    (2, 'Test Storage 4', 4, 1, 0, 'TestNote'),
    (3, 'Test Storage 2', 2, 1, 0, 'TestNote'),
    (4, 'Test Storage 3', 3, 1, 0, 'TestNote'),
    (5, 'Test Storage 7', 7, 2, 0, 'TestNote'),
    (6, 'Test Storage 6', 6, 2, 0, 'TestNote'),
    (7, 'Test Storage 5', 5, 2, 0, 'TestNote')
) AS v (storage_facility_id, location, battery_tier_id, capacity, usage, notes)
WHERE NOT EXISTS (
    SELECT 1 FROM StorageSvcSchema.StorageFacilities
);


