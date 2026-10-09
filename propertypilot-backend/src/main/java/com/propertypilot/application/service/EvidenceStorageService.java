package com.propertypilot.application.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import java.util.Locale;
import java.util.Set;
import java.util.UUID;

@Service
public class EvidenceStorageService {
    private static final long MAX_BYTES = 25L * 1024 * 1024;
    private static final Set<String> ALLOWED_MEDIA_TYPES = Set.of(
            "image/jpeg", "image/png", "image/webp", "image/heic", "image/heif",
            "video/mp4", "video/quicktime", "application/pdf", "text/plain",
            "application/msword", "application/vnd.openxmlformats-officedocument.wordprocessingml.document");
    private final Path root;

    public EvidenceStorageService(@Value("${propertypilot.storage.evidence-directory:./data/evidence}") String directory) {
        this.root = Path.of(directory).toAbsolutePath().normalize();
    }

    public String store(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) throw new IllegalArgumentException("Choose a non-empty file");
        if (file.getSize() > MAX_BYTES) throw new IllegalArgumentException("Evidence files cannot exceed 25 MB");
        String mediaType = file.getContentType() == null ? "application/octet-stream" : file.getContentType().toLowerCase(Locale.ROOT);
        if (!ALLOWED_MEDIA_TYPES.contains(mediaType)) throw new IllegalArgumentException("This file type is not supported");
        Files.createDirectories(root);
        String original = file.getOriginalFilename() == null ? "" : Path.of(file.getOriginalFilename()).getFileName().toString();
        String extension = original.contains(".") ? original.substring(original.lastIndexOf('.') + 1).replaceAll("[^A-Za-z0-9]", "") : "bin";
        String key = UUID.randomUUID() + (extension.isBlank() ? "" : "." + extension.toLowerCase(Locale.ROOT));
        Path target = root.resolve(key).normalize();
        if (!target.startsWith(root)) throw new IllegalArgumentException("Invalid file name");
        try (var input = file.getInputStream()) {
            Files.copy(input, target);
        } catch (IOException | RuntimeException failure) {
            try {
                Files.deleteIfExists(target);
            } catch (IOException cleanupFailure) {
                failure.addSuppressed(cleanupFailure);
            }
            throw failure;
        }
        return key;
    }

    public byte[] read(String key) throws IOException {
        Path target = root.resolve(key).normalize();
        if (!target.startsWith(root) || !Files.isRegularFile(target)) throw new java.nio.file.NoSuchFileException("Evidence file not found");
        return Files.readAllBytes(target);
    }

    public boolean exists(String key) {
        if (key == null || key.isBlank()) return false;
        Path target = root.resolve(key).normalize();
        return target.startsWith(root) && Files.isRegularFile(target);
    }

    public void delete(String key) throws IOException {
        Path target = root.resolve(key).normalize();
        if (target.startsWith(root)) Files.deleteIfExists(target);
    }
}
