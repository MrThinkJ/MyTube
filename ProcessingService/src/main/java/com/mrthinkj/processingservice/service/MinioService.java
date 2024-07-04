package com.mrthinkj.processingservice.service;

import io.minio.GetObjectResponse;
import io.minio.messages.Item;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.InputStream;
import java.util.List;

public interface MinioService {
    void putObjectByFile(File file, String fileName) throws Exception;
    void putObject(MultipartFile file, String fileName) throws Exception;
    void putFolder(String folderName) throws Exception;
    List<Item> listFile(String bucket, String prefix) throws Exception;
    GetObjectResponse getObject(String bucket, String objectName) throws Exception;
    void removeObject(String bucket, String objectName) throws Exception;
}
