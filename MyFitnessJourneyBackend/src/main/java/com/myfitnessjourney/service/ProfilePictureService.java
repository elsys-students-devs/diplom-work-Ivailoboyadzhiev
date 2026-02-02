package com.myfitnessjourney.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;

@Service
@Slf4j
public class ProfilePictureService {

    private static final Set<String> ALLOWED_CONTENT_TYPES = Set.of(
            "image/jpeg", "image/png", "image/gif", "image/webp"
    );
    private static final long MAX_FILE_SIZE_BYTES = 5 * 1024 * 1024; // 5 MB

    private final Path profilesDir;

    public ProfilePictureService(@Value("${app.upload.dir:uploads}") String uploadDir) {
        this.profilesDir = Paths.get(uploadDir).resolve("profiles").toAbsolutePath();
        try {
            Files.createDirectories(profilesDir);
        } catch (IOException e) {
            log.error("Failed to create profiles upload directory: {}", profilesDir, e);
            throw new IllegalStateException("Could not create upload directory", e);
        }
    }

    
     //Saves the uploaded file for the given user and returns the relative URL path
     
    public String saveProfilePicture(Long userId, MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File is required");
        }
        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_CONTENT_TYPES.contains(contentType)) {
            throw new IllegalArgumentException("Invalid file type. Allowed: JPEG, PNG, GIF, WebP");
        }
        if (file.getSize() > MAX_FILE_SIZE_BYTES) {
            throw new IllegalArgumentException("File size must not exceed 5 MB");
        }

        String extension = getExtension(contentType);
        String filename = userId + "." + extension;
        Path targetFile = profilesDir.resolve(filename);

        Files.write(targetFile, file.getBytes());
        log.info("Saved profile picture for user {} at {}", userId, targetFile);

        return "profiles/" + filename;
    }

    public Path getProfilePicturePath(String filename) {
        if (filename == null || filename.isBlank() || filename.contains("..")) {
            return null;
        }
        // Resolve only the file name within the profiles directory to prevent path traversal
        Path candidate = profilesDir.resolve(Paths.get(filename).getFileName()).normalize();
        if (!candidate.startsWith(profilesDir.normalize())) {
            return null;
        }
        return candidate;
    }

    private static String getExtension(String contentType) {
        return switch (contentType) {
            case "image/png" -> "png";
            case "image/gif" -> "gif";
            case "image/webp" -> "webp";
            default -> "jpg";
        };
    }
}
