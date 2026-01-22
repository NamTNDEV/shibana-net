package com.shibana.media_service.controller;

import com.shibana.media_service.dto.response.ApiResponse;
import com.shibana.media_service.dto.response.FileResponse;
import com.shibana.media_service.dto.response.UploadedMediaResponse;
import com.shibana.media_service.service.MediaService;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = lombok.AccessLevel.PRIVATE)
public class MediaController {
    MediaService mediaService;

    @PostMapping("/upload")
    public  ApiResponse<UploadedMediaResponse> uploadFile(
            @RequestParam("file") MultipartFile file,
            @AuthenticationPrincipal Jwt jwt
    ) {
        String authorId = jwt.getClaimAsString("user_id");
        return ApiResponse.<UploadedMediaResponse>builder()
                .code(200)
                .data(mediaService.uploadFile(file, authorId))
                .message("File uploaded successfully")
                .build();
    }

    @GetMapping("/static/{fileName}")
    public ResponseEntity<Resource> staticFile(@PathVariable String fileName) {
        FileResponse fileResponse = mediaService.staticFile(fileName);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, fileResponse.getContentType())
                .body(fileResponse.getFile());
    }
}
