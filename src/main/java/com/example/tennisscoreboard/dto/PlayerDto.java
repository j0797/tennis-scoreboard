package com.example.tennisscoreboard.dto;

import lombok.Value;

@Value
public class PlayerDto {

    // для DTO идеально подходит record

    Long id;
    String name;
}
