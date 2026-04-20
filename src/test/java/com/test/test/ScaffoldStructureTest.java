package com.test.test;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;

class ScaffoldStructureTest {

  @Test
  void scaffoldDirectoriesShouldExist() {
    String[] expectedDirectories = {
      "src/main/java/com/test/test/bootstrap",
      "src/main/java/com/test/test/common",
      "src/main/java/com/test/test/infrastructure",
      "src/main/java/com/test/test/domain/auth/api",
      "src/main/java/com/test/test/domain/auth/application",
      "src/main/java/com/test/test/domain/auth/domain",
      "src/main/java/com/test/test/domain/auth/infrastructure",
      "src/main/java/com/test/test/domain/auth/convert",
      "src/main/java/com/test/test/domain/user/api",
      "src/main/java/com/test/test/domain/user/application",
      "src/main/java/com/test/test/domain/user/domain",
      "src/main/java/com/test/test/domain/user/infrastructure",
      "src/main/java/com/test/test/domain/user/convert",
      "src/main/java/com/test/test/domain/stream/api",
      "src/main/java/com/test/test/domain/stream/application",
      "src/main/java/com/test/test/domain/stream/domain",
      "src/main/java/com/test/test/domain/stream/infrastructure",
      "src/main/java/com/test/test/domain/stream/convert",
      "src/test/java/com/test/test/common",
      "src/test/java/com/test/test/infrastructure",
      "src/test/java/com/test/test/domain/auth",
      "src/test/java/com/test/test/domain/user",
      "src/test/java/com/test/test/domain/stream"
    };

    for (String directory : expectedDirectories) {
      assertTrue(
          Files.isDirectory(Path.of(directory)), () -> "Expected directory to exist: " + directory);
    }
  }
}
