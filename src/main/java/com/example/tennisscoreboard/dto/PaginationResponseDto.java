package com.example.tennisscoreboard.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class PaginationResponseDto<T> {

    // для DTO идеально подходит record

    // можно добавить поле currentPage, чтобы передавать все данные во view в одном DTO

    // общее число страниц не может быть null (всегда есть какое-то число страниц), поэтому вместо обёртки Long можно использовать примитивный тип long

    private List<T> items;
    private Long totalPages;
}
