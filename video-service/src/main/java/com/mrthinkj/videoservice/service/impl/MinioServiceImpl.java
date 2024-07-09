package com.mrthinkj.videoservice.service.impl;

import com.mrthinkj.videoservice.config.StorageConfiguration;
import com.mrthinkj.videoservice.service.MinioService;
import io.minio.*;
import io.minio.messages.Item;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class MinioServiceImpl implements MinioService {
    MinioClient minioClient;
    StorageConfiguration storageConfiguration;
    @Override
    public void putObject(MultipartFile file, String fileName) throws Exception {
        InputStream stream = file.getInputStream();
        // Save video content to MinIO
        minioClient.putObject(PutObjectArgs
                .builder()
                .bucket(storageConfiguration.getBucketStore())
                .object(fileName)
                .stream(stream, file.getSize(), -1)
                .contentType(file.getContentType())
                .build());
        stream.close();
    }

    @Override
    public void putFolder(String folderName) throws Exception{
        minioClient.putObject(PutObjectArgs.builder()
                .bucket(storageConfiguration.getBucketStore())
                .object(folderName)
                .stream(new ByteArrayInputStream(new byte[] {}), 0, -1)
                .build());
    }

    @Override
    public List<Item> listFile(String bucket, String prefix) throws Exception{
        List<Item> results = new ArrayList<>();
        for (Result<Item> itemResult : minioClient.listObjects(ListObjectsArgs.builder()
                        .bucket(bucket)
                        .prefix(prefix)
                        .recursive(false)
                        .build())){
            Item i = itemResult.get();
            if (i.isDir())
                continue;
            results.add(i);
        }
        return results;
    }

    @Override
    public GetObjectResponse getObject(String bucket, String objectName) throws Exception {
        return minioClient.getObject(GetObjectArgs.builder()
                        .bucket(bucket)
                        .object(objectName)
                .build());
    }

    @Override
    public void removeObject(String bucket, String objectName) throws Exception{
        minioClient.removeObject(RemoveObjectArgs.builder()
                .bucket(bucket)
                .object(objectName)
                .build());
    }
}
