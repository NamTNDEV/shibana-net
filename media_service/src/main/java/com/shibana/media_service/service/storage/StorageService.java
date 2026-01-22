package com.shibana.media_service.service.storage;

import org.springframework.core.io.Resource;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;

public interface StorageService {
    void store(InputStream inputStream, String fileName) throws IOException;
    Resource read(Path filePath) throws IOException;
}
