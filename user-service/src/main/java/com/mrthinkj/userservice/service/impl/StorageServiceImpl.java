package com.mrthinkj.userservice.service.impl;

import com.mrthinkj.userservice.service.StorageService;
import com.mrthinkj.userservice.utils.WebUtils;
import lombok.AllArgsConstructor;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
@AllArgsConstructor
public class StorageServiceImpl implements StorageService {
    private static final Path UPLOAD = Paths.get(WebUtils.UPLOAD_FOLDER);
    private static final Path BIN = Paths.get(WebUtils.BIN_FOLDER);
    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());
    @Override
    public void initFolder() {
        if (!Files.exists(UPLOAD)){
            try{
                Files.createDirectories(UPLOAD);
            } catch (IOException e){
                LOGGER.error("Error when init upload folder");
            }
        }
        if (!Files.exists(BIN)){
            try{
                Files.createDirectories(BIN);
            } catch (IOException e){
                LOGGER.error("Error when init bin folder");
            }
        }
    }

    @Override
    public String saveFile(MultipartFile file) {
        String fileUUID = UUID.randomUUID().toString();
        String fileName = fileUUID+"."+ FilenameUtils.getExtension(file.getOriginalFilename());
        try{
            Files.copy(file.getInputStream(), UPLOAD.resolve(fileName));
        } catch (IOException e){
            LOGGER.error("Error when save file to upload file: {}", e.getMessage());
            throw new RuntimeException("");
        }
        return fileName;
    }

    @Override
    public Resource getFile(String fileName) {
        try {
            Path filePath = UPLOAD.resolve(fileName);
            Resource fileResource = new UrlResource(filePath.toUri());
            if (fileResource.exists() && fileResource.isReadable()){
                return fileResource;
            } else {
                throw new RuntimeException("Could not read the file!");
            }
        } catch (MalformedURLException e){
            LOGGER.error("Error when get file: {}", e.getMessage());
            throw new RuntimeException("Could not get the resource");
        }
    }

    @Override
    public void deleteFile(String fileName) {
        try{
            Path filePath = UPLOAD.resolve(fileName);
            Files.copy(filePath, BIN);
            Files.deleteIfExists(filePath);
        } catch (IOException e){
            LOGGER.error("Error when delete file: {}", e.getMessage());
            throw new RuntimeException("Could not delete this file");
        }
    }
}
