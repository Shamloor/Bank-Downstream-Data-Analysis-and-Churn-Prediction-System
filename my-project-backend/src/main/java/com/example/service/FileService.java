package com.example.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.*;

@Service
public class FileService {

    private final Path rootLocation = Paths.get("D:/Programming/Front-End-Modified/my-project-frontend/src/views/files");

    public FileService() {
        try {
            Files.createDirectories(rootLocation);
        } catch (IOException e) {
            throw new RuntimeException("Could not initialize storage location", e);
        }
    }

    public void store(MultipartFile file) {
        try {
            if (file.isEmpty()) {
                throw new RuntimeException("Failed to store empty file.");
            }
            Path destinationFile = rootLocation.resolve(
                            Paths.get(file.getOriginalFilename()))
                    .normalize().toAbsolutePath();
            if (!destinationFile.getParent().equals(rootLocation.toAbsolutePath())) {
                throw new RuntimeException(
                        "Cannot store file outside current directory.");
            }
            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, destinationFile,
                        StandardCopyOption.REPLACE_EXISTING);
            }
            System.out.println("fuck");
        } catch (IOException e) {
            throw new RuntimeException("Failed to store file.", e);
        }
    }

    public String loadFileContent(String filename) {
        try {
            Path file = rootLocation.resolve(filename);
            return new String(Files.readAllBytes(file));
        } catch (IOException e) {
            throw new RuntimeException("Failed to read file.", e);
        }
    }
}
