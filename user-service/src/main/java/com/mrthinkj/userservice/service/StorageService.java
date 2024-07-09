package com.mrthinkj.userservice.service;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

public interface StorageService {
    void initFolder();
    String saveFile(MultipartFile file);
    Resource getFile(String fileName);
    void deleteFile(String fileName);
}
