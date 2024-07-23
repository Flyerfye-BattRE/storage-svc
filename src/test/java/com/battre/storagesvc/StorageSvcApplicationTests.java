package com.battre.storagesvc;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = "grpc.server.port=9030")
class StorageSvcApplicationTests {
  @Test
  void contextLoads() {}
}
