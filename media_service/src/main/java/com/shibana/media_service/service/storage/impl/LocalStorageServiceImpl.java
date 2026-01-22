package com.shibana.media_service.service.storage.impl;

import com.shibana.media_service.exception.AppException;
import com.shibana.media_service.exception.ErrorCode;
import com.shibana.media_service.service.storage.StorageService;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

@Slf4j
@Service
@Primary
@FieldDefaults(makeFinal = true, level = lombok.AccessLevel.PRIVATE)
public class LocalStorageServiceImpl implements StorageService {
    @NonFinal
    @Value("${media.public-upload-dir}")
    String PUBLIC_UPLOAD_DIR;

    Path getPublicUploadDir() {
        Path uploadDir = Path.of(PUBLIC_UPLOAD_DIR).toAbsolutePath().normalize();
        if (!Files.exists(uploadDir)) {
            log.error("Upload failed: upload directory does not exist");
            throw new AppException(ErrorCode.FILE_NOT_FOUND);
        }
        return uploadDir;
    }

    @Override
    public void store(InputStream inputStream, String fileName) throws IOException {
        var targetPath = getPublicUploadDir().resolve(fileName).normalize();
        Files.copy(inputStream, targetPath, StandardCopyOption.REPLACE_EXISTING);
    }

    @Override
    public Resource read(Path filePath) throws IOException {
        Files.readAllBytes(filePath);
        return null;
    }
}
