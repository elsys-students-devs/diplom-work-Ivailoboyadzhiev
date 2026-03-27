package com.myfitnessjourney.controller;

import com.myfitnessjourney.service.ProfilePictureService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/uploads")
@RequiredArgsConstructor
public class UploadsController {

    private final ProfilePictureService profilePictureService;

    @GetMapping("/profiles/{filename:.+}")
    public ResponseEntity<Resource> getProfilePicture(@PathVariable String filename) {
        if (!profilePictureService.isValidProfilePictureFilename(filename)) {
            return ResponseEntity.badRequest().build();
        }
        return profilePictureService.getProfilePicture(filename)
                .map(profilePicture -> ResponseEntity.ok()
                        .contentType(MediaType.parseMediaType(profilePicture.contentType()))
                        .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + filename + "\"")
                        .body(profilePicture.resource()))
                .orElse(ResponseEntity.notFound().build());
    }
}
