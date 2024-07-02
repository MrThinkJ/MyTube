package com.mrthinkj.videoservice.payload;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class VideoUploadDTO {
    private String videoName;
    private Long posterId;
    private String title;
    private MultipartFile videoContent;
}
