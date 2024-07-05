package com.mrthinkj.videoservice.payload;

import com.mrthinkj.videoservice.annotation.ValidImage;
import com.mrthinkj.videoservice.annotation.ValidVideo;
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
    private Long posterId;
    private String title;
    @ValidImage
    private MultipartFile thumbnail;
    @ValidVideo
    private MultipartFile videoContent;
}
