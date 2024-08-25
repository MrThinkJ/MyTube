package com.mrthinkj.userservice.payload;

import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PagedUsersResponse {
    private List<UserResponseDTO> content;
    private int page;
    private int size;
    private int totalPages;
    private int totalElements;
    private boolean isLastPage;
}
