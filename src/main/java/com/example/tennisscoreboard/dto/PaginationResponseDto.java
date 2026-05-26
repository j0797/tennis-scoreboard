package com.example.tennisscoreboard.dto;

import java.util.List;

public record PaginationResponseDto<T>(List<T> items, Long totalPages) {
}