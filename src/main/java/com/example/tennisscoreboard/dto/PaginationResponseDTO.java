package com.example.tennisscoreboard.dto;

import com.example.tennisscoreboard.entity.Match;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class PaginationResponseDTO {
    private List<Match> matches;
    private Long totalPages;
}
