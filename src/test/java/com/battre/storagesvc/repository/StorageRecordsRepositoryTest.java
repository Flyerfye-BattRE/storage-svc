package com.battre.storagesvc.repository;

import java.util.logging.Logger;
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

  // No repository fns => No repository tests
}
