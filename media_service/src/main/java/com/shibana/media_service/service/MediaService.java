package com.shibana.media_service.service;

import com.shibana.media_service.dto.response.FileResponse;
import com.shibana.media_service.dto.response.UploadedMediaResponse;
import com.shibana.media_service.entity.Media;
import com.shibana.media_service.enums.StorageType;
import com.shibana.media_service.exception.AppException;
import com.shibana.media_service.exception.ErrorCode;
import com.shibana.media_service.repo.MediaRepo;
import com.shibana.media_service.service.storage.StorageService;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.time.Instant;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = lombok.AccessLevel.PRIVATE)
public class MediaService {
    MediaRepo mediaRepo;
    StorageService storageService;

    @NonFinal
    @Value("${media.public-access-url}")
    String PUBLIC_ACCESS_URL;

    private String generateUniqueFileName(String originalFileName) {
        String fileExtension = StringUtils.getFilenameExtension(originalFileName);
        return UUID.randomUUID() + (fileExtension != null ? "." + fileExtension : "");
    }

    public UploadedMediaResponse uploadFile(MultipartFile file, String authorId) {
        String originalFileName = file.getOriginalFilename();
        String newFileName = generateUniqueFileName(originalFileName);

        try (InputStream inputStream = file.getInputStream()) {
            storageService.store(inputStream, newFileName);
        } catch (IOException e) {
            log.error("File upload failed: {}", e.getMessage());
            throw new AppException(ErrorCode.FILE_UPLOAD_FAILED);
        }

        Media media = Media.builder()
                .fileName(newFileName)
                .originalName(originalFileName)
                .size(file.getSize())
                .url("/media/" + newFileName)
                .createdAt(Instant.now())
                .storageType(StorageType.LOCAL.name())
                .ownerId(authorId)
                .contentType(file.getContentType())
                .build();

        mediaRepo.save(media);

        return UploadedMediaResponse.builder()
                .mediaId(media.getId())
                .fileName(media.getFileName())
                .url(PUBLIC_ACCESS_URL + media.getFileName())
                .storageType(media.getStorageType())
                .build();
    }

    public FileResponse staticFile(String fileName) {
        var uploadedMediaMetadata = mediaRepo.findByFileName(fileName)
                .orElseThrow(() -> {
                    log.error("Static file not found: {}", fileName);
                    return new AppException(ErrorCode.FILE_NOT_FOUND);
                });

        try {
            Resource fileResource = storageService.read(fileName);
            return FileResponse.builder()
                    .file(fileResource)
                    .contentType(uploadedMediaMetadata.getContentType())
                    .build();
        } catch (IOException e) {
            log.error("Error reading static file: {}", e.getMessage());
            throw new AppException(ErrorCode.FILE_NOT_FOUND);
        }
    }

}
