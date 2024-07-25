package com.mrthinkj.core.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NewVideoNotification {
    private String videoUUID;
    private String videoTitle;
    private Long videoOwnerId;
}
