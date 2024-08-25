package com.mrthinkj.core.entity;

import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NewVideoNotification {
    private String videoUUID;
    private String videoTitle;
    private Long videoOwnerId;
}
