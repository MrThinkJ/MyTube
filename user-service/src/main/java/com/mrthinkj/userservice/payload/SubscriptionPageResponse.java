package com.mrthinkj.userservice.payload;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SubscriptionPageResponse {
    private List<UserResponseDTO> userResponseDTOS;
    private int pageNumber;
    private int pageSize;
    private int totalPages;
    private int totalElements;
    private boolean isLastPage;

}
