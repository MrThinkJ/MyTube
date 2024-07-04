package com.mrthinkj.processingservice.service.impl;

import com.mrthinkj.processingservice.config.StorageConfiguration;
import com.mrthinkj.processingservice.entity.ProcessedVideoEvent;
import com.mrthinkj.processingservice.repository.ProcessedVideoEventRepository;
import com.mrthinkj.processingservice.service.MinioService;
import com.mrthinkj.processingservice.service.ProcessVideoService;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@AllArgsConstructor
public class ProcessVideoServiceImpl implements ProcessVideoService {
    ProcessedVideoEventRepository processedVideoEventRepository;
    StorageConfiguration storageConfiguration;
    MinioService minioService;
    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());
    @Override
    public boolean checkIfExistIdempotencyKey(String idempotencyKey) {
        ProcessedVideoEvent processedVideoEvent = processedVideoEventRepository.findByIdempotencyKey(idempotencyKey);
        return processedVideoEvent != null;
    }

    @Override
    public void processVideo(String videoUUID) throws Exception {
        InputStream inputStream = minioService.getObject(storageConfiguration.getBucketStore(), videoUUID);
        processVideoToHls(inputStream, videoUUID);
    }

    private void processVideoToHls(InputStream inputStream, String folderName) throws IOException, InterruptedException {
        File tempFile = File.createTempFile("temp", ".mp4");
        try (FileOutputStream outputStream = new FileOutputStream(tempFile)) {
            inputStream.transferTo(outputStream);
        }
        List<String> commands = mp4ToHlsCommand(tempFile.getAbsolutePath());
        String dataPath = "D:\\Study\\Project\\MyTube\\data";
        String videoFolderPath = dataPath +"/"+folderName;
        File videoFolder = new File(videoFolderPath);
        if (videoFolder.mkdir() || videoFolder.isDirectory()){
            Process process = new ProcessBuilder().command(commands).directory(videoFolder).start();
            CountDownLatch latch = new CountDownLatch(1);
            new Thread(
                    () -> {
                        try (BufferedReader bufferedReader =
                                     new BufferedReader(new InputStreamReader(process.getErrorStream()))) {
                            String line;
                            while ((line = bufferedReader.readLine()) != null) {
                                LOGGER.info("********* Input Stream: "+line);
                            }
                        } catch (IOException e) {
                            LOGGER.error("Error reading FFmpeg output:", e);
                        } finally {
                            latch.countDown();
                        }
                    })
                    .start();
            latch.await();
        }
        uploadFolderToMinIO(videoFolderPath, folderName);
        LOGGER.info("******** DONE ********");
    }

    private void uploadFolderToMinIO(String localFolderPath, String minioFolder){
        File localFolder = new File(localFolderPath);
        File[] files = localFolder.listFiles();
        if (files != null){
            for (File file : files){
                try{
                    String minioFilePath = minioFolder+"/"+file.getName();
                    minioService.putObjectByFile(file, minioFilePath);
                } catch (Exception e){
                    LOGGER.error("Error when convert from file to InputStream");
                }
            }
        }
    }

    private List<String> mp4ToHlsCommand(String src) {
        List<String> command = new ArrayList<>();
        command.add("ffmpeg");
        command.add("-threads");
        command.add("2");
        command.add("-i");
        command.add(src);
        command.add("-hls_time");
        command.add("10");
        command.add("-hls_list_size");
        command.add("0");
        command.add("-hls_segment_filename");
        command.add("%d.ts");
        command.add("index.m3u8");
        return command;
    }
}
