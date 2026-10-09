package com.propertypilot.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.stream.Stream;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

class EvidenceStorageServiceTest {
    private Path tempDirectory;

    @BeforeEach
    void createTestStorageDirectory() throws IOException {
        tempDirectory = Files.createTempDirectory(Path.of("target"), "test-evidence-storage-");
    }

    @AfterEach
    void removeTestStorageDirectory() throws IOException {
        if (Files.exists(tempDirectory)) {
            try (Stream<Path> paths = Files.walk(tempDirectory)) {
                for (Path path : paths.sorted(Comparator.reverseOrder()).toList()) {
                    Files.deleteIfExists(path);
                }
            }
        }
    }

    @Test
    void storesReadsAndDeletesOnlyGeneratedKeys() throws Exception {
        EvidenceStorageService storage = new EvidenceStorageService(tempDirectory.toString());
        MockMultipartFile file = new MockMultipartFile("file", "room.jpg", "image/jpeg", new byte[] { 1, 2, 3 });

        String key = storage.store(file);

        assertThat(key).endsWith(".jpg");
        assertThat(storage.exists(key)).isTrue();
        assertThat(storage.read(key)).containsExactly(1, 2, 3);
        assertThat(storage.exists("../room.jpg")).isFalse();
        storage.delete(key);
        assertThat(storage.exists(key)).isFalse();
    }

    @Test
    void rejectsEmptyAndUnsupportedUploads() {
        EvidenceStorageService storage = new EvidenceStorageService(tempDirectory.toString());
        MockMultipartFile empty = new MockMultipartFile("file", "empty.jpg", "image/jpeg", new byte[0]);
        MockMultipartFile unsupported = new MockMultipartFile("file", "script.js", "text/javascript", new byte[] { 1 });

        assertThatThrownBy(() -> storage.store(empty)).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> storage.store(unsupported)).isInstanceOf(IllegalArgumentException.class);
    }
}
