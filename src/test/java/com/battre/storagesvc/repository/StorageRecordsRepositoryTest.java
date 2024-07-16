package com.battre.storagesvc.repository;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.logging.Logger;

import com.battre.storagesvc.model.StorageRecordType;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@DataJpaTest
public class StorageRecordsRepositoryTest {
  private static final Logger logger =
      Logger.getLogger(StorageRecordsRepositoryTest.class.getName());

  @Autowired private StorageRecordsRepository storageRecRepo;
  @PersistenceContext
  private EntityManager entityManager;

  @Test
  public void testEndStorageForBatteryId() {
    int storage_id = 2;
    int batteryId = 6;
    Timestamp storageEndDate = Timestamp.from(Instant.now());

    // End storage for the battery with the given ID
    storageRecRepo.endStorageForBatteryId(batteryId, storageEndDate);

    // Flush and clear the persistence context to ensure the changes are written to the database
    entityManager.flush();
    entityManager.clear();

    // Retrieve the record to verify the update
    StorageRecordType record = entityManager.find(StorageRecordType.class, storage_id);

    // Verify the result
    assertNotNull(record);
    // Converted to long and truncated bc sometimes the nanoseconds are not formatted correctly in the response
    assertEquals(storageEndDate.getTime()/ 1000 * 1000, record.getStorageEndDate().getTime()/ 1000 * 1000);
  }
}
