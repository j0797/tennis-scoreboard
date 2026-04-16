package com.example.tennisscoreboard.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class PaginationResponseDto<T> {
    private List<T> items;
    private Long totalPages;
}
