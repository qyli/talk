package com.test.test;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;

class ScaffoldResourceLayoutTest {

  @Test
  void resourceProfilesShouldExist() {
    String[] expectedFiles = {
      "src/main/resources/application.yml",
      "src/main/resources/application-dev.yml",
      "src/main/resources/application-test.yml",
      "src/main/resources/application-prod.yml"
    };

    for (String file : expectedFiles) {
      assertTrue(Files.exists(Path.of(file)), () -> "Expected file to exist: " + file);
    }

    assertTrue(
        Files.isDirectory(Path.of("src/main/resources/db/migration")),
        "Expected db migration directory to exist");
  }
}
