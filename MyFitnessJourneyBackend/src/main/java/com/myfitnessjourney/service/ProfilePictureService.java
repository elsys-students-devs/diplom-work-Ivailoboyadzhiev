package com.myfitnessjourney.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.Set;

@Service
@Slf4j
public class ProfilePictureService {

    private static final Set<String> ALLOWED_CONTENT_TYPES = Set.of(
            "image/jpeg", "image/png", "image/gif", "image/webp"
    );
    private static final long MAX_FILE_SIZE_BYTES = 5 * 1024 * 1024; // 5 MB
    private static final String FILENAME_PATTERN = "[a-zA-Z0-9._-]+";

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

    public boolean isValidProfilePictureFilename(String filename) {
        return filename != null && filename.matches(FILENAME_PATTERN);
    }

    public Optional<ProfilePictureResource> getProfilePicture(String filename) {
        if (!isValidProfilePictureFilename(filename)) {
            throw new IllegalArgumentException("Invalid filename");
        }
        Path path = getProfilePicturePath(filename);
        if (path == null || !path.toFile().exists()) {
            return Optional.empty();
        }
        try {
            Resource resource = new UrlResource(path.toUri());
            String contentType = getContentTypeFromFilename(filename);
            return Optional.of(new ProfilePictureResource(resource, contentType));
        } catch (Exception e) {
            log.warn("Could not load profile picture: {}", filename, e);
            return Optional.empty();
        }
    }

    public static String getContentTypeFromFilename(String filename) {
        String lower = filename.toLowerCase();
        if (lower.endsWith(".png")) return "image/png";
        if (lower.endsWith(".gif")) return "image/gif";
        if (lower.endsWith(".webp")) return "image/webp";
        return "image/jpeg";
    }

    public record ProfilePictureResource(Resource resource, String contentType) {}

    private static String getExtension(String contentType) {
        return switch (contentType) {
            case "image/png" -> "png";
            case "image/gif" -> "gif";
            case "image/webp" -> "webp";
            default -> "jpg";
        };
    }
}
